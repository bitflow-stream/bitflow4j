package metrics.algorithms.clustering.clustering;


import com.yahoo.labs.samoa.instances.Instance;
import metrics.Sample;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.algorithms.clustering.MOAStreamClusterer;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.DoubleStream;
/**
 * @author mbyfield
 * This abstract class can be extended by all MOA clustering algorithms, that use sphere clusters.
 * This class implements the calculation of the distance between samples and the nearest cluster.
 *
 */
public abstract class MOASphereClusterer<T extends AbstractClusterer & Serializable> extends MOAStreamClusterer<T> {

    protected boolean calculateDistance;
    private boolean alwaysAddDistanceMetrics = false;

    public MOASphereClusterer(T clusterer){
        super(clusterer);
    }

    /**
     * This constructor is used to train on all labels or the default label set.
     * @param clusterer The clusterer, must extend @link moa.clusterers.AbstractClusterer.
     * @param calculateDistance If true, the distance to the nearest cluster will be calculated
     *                          and appended to the sample metrics using the distance prefix
     *                          {@link ClusterConstants#DISTANCE_PREFIX}
     */
    public MOASphereClusterer(T clusterer, boolean calculateDistance) {
        super(clusterer);
        this.calculateDistance = calculateDistance;
    }

    protected Map.Entry<Double, double[]> getDistance(Instance instance, Clustering clustering) throws IOException {
        Map.Entry<Double, double[]> distance = null;
        Optional<Map.Entry<Double, double[]>> distanceT;
        try {
            distanceT = clustering.getClustering().stream().map(cluster -> {
                try {
                    Field field = cluster.getClass().getDeclaredField("radius");
                    field.setAccessible(true);
                    double radius = field.getDouble(cluster);
                    return distance(cluster.getCenter(), instance.toDoubleArray(), radius);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }).min((entry1, entry2) -> Double.compare(entry1.getKey(), entry2.getKey()));
        } catch (IllegalStateException e) {
            throw new IOException(e);
        }
        if (distanceT.isPresent()) distance = distanceT.get();
        return distance;
    }

    @Override
    protected Clustering getClusteringResult() {
        // TODO this used to be getMicroClusteringResult()
        // return clusterer.getClusteringResult();
        return clusterer.getMicroClusteringResult();
    }

    /**
     * This method calculates the distance between a given sample and the center of the nearest cluster.
     * It can only be used for moa clustering algorithms that use sphere clusters internally.
     * @param center The center of the cluster.
     * @param doubles The metrics of the {@link Instance}.
     * @param radius The radius of the sphere cluster
     * @return The result is a Map.Entry<Double, double[]>.
     * The key is the overall distance and the double array contains the distances in each dimension.
     */
    protected Map.Entry<Double, double[]> distance(double[] center, double[] doubles, double radius) {
        if (center.length != doubles.length || center.length == 0) {
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN: " + center.length + " vs " + doubles.length);
        }
        double distanceCenter;
        Double distance;
        double[] distances = new double[center.length - 1];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Math.abs(center[i] - doubles[i]);
        }
        distanceCenter = Math.sqrt(DoubleStream.of(distances).map(dist -> dist * dist).sum());
        distance = distanceCenter - radius;
        double factor = distance / distanceCenter;
        for (int i = 0; i < distances.length; i++) {
            distances[i] = distances[i] * factor;
        }
        return new TreeMap.SimpleEntry<>(distance, distances);
    }

    /**
     * Returns the clusterId for the provided instance. For sphere clusterers the cluster id with the highest inclusion probability is returned or -1 for noise.
     * @param instance the instance for the current sample
     * @return the cluster id with the highest inclusion probability, or -1 for noise.
     */
    @Override
    protected int calculateCluster(Instance instance) {
        if (clusteringResult == null) return -1;
        AutoExpandVector<moa.cluster.Cluster> clustering = clusteringResult.getClustering();
        double inclusionProbability = 0.0;
        int bestFitCluster = -1;
        int clusterNum = 0;
        for (moa.cluster.Cluster c : clustering) {
            double clusterInclusionProbability = c.getInclusionProbability(instance);
            if (inclusionProbability < clusterInclusionProbability) {
                inclusionProbability = clusterInclusionProbability;
                bestFitCluster = clusterNum;
            }
            clusterNum++;
        }
        return bestFitCluster;
    }

    public MOAStreamClusterer<T> calculateDistance(){
        this.calculateDistance = true;
        return this;
    }


    /**
     * Calculate the distance to the nearest real cluster for samples in the noise cluster
     * @param instance the instance in the cluster
     * @param bestFitCluster the id of the best fit cluster
     * @return  A {@link Map.Entry} containing the overall distance to the closest cluster and the an array holding the distances for each dimension.
     */
    protected Map.Entry<Double, double[]> calculateDistances(Instance instance, int bestFitCluster){
        if (clusteringResult != null && calculateDistance && bestFitCluster == -1) {
            try {
                return getDistance(instance, clusteringResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }return null;
    }

    /**
     * Append distance metrics to the sample, giving the distance to the closest cluster.
     * @param sample The current sample
     * @param distance The overall distance and an array for the distances in each dimension
     * @return the extended sample
     */
    protected Sample appendDistance(Sample sample, Map.Entry<Double, double[]> distance) {
        if (distance == null && alwaysAddDistanceMetrics) {
            // Output only -1 values for all distances.
            Map<Double, double[]> distanceMap = new HashMap<>();
            double mockDistances[] = new double[sample.getMetrics().length];
            Arrays.fill(mockDistances, -1.0);
            distanceMap.put(-1.0, mockDistances);
            distance = distanceMap.entrySet().<Map.Entry<Double, double[]>>toArray(new Map.Entry[1])[0];
        }
        // distance can be changed in the block above
        if (distance != null) {
            String newHeader[] = new String[distance.getValue().length + 1];
            double newValues[] = new double[newHeader.length];
            for (int i = 0; i < newHeader.length - 1; i++) {
                newHeader[i] = ClusterConstants.DISTANCE_PREFIX + sample.getHeader().header[i];
                newValues[i] = distance.getValue()[i];
            }
            newHeader[newHeader.length - 1] = ClusterConstants.DISTANCE_PREFIX + "overall";
            newValues[newValues.length - 1] = distance.getKey();
            sample = sample.extend(newHeader, newValues);
        }
        return sample;
    }


    /**
     * If calculateDistance is true then set distance-metrics to -1.0 when
     * the sample is inside a known cluster (no real distance can be calculated).
     */
    public MOAStreamClusterer alwaysAddDistanceMetrics() {
        alwaysAddDistanceMetrics = true;
        return this;
    }

    @Override
    protected Sample sampleClustered(Sample sample, Instance instance, int bestFitCluster) {
        return appendDistance(sample, calculateDistances(instance, bestFitCluster));
    }
}
