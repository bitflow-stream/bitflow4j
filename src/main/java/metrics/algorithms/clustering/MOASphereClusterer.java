package metrics.algorithms.clustering;


import com.yahoo.labs.samoa.instances.Instance;
import moa.cluster.*;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.DoubleStream;
/**
 * @author mbyfield
 * This abstract class can be extended by all MOA clustering algorithms, that use sphere clusters.
 * This class implements the calculation of the distance between samples and the nearest cluster.
 *
 */
public abstract class MOASphereClusterer<T extends AbstractClusterer & Serializable> extends MOAStreamClusterer<T> {

    /**
     * This constructor is used to train on all labels or the default label set.
     * @param clusterer The clusterer, must extend @link moa.clusterers.AbstractClusterer.
     * @param alwaysTrain If true, all samples will be used to train the model, if false, only the default configuration is loaded (see {@link MOAStreamClusterer}).
     * @param calculateDistance If true, the distance to the nearest cluster will be calculated and appended to the sample metrics using the distance prefix @link {@link ClusterConstants#DISTANCE_PREFIX}
     */
    public MOASphereClusterer(T clusterer, boolean alwaysTrain, boolean calculateDistance) {
        super(clusterer, alwaysTrain, calculateDistance);
    }

    /**
     * This constructor is used to train on a given set of labels.
     * @param clusterer The clusterer, must extend @link moa.clusterers.AbstractClusterer.
     * @param trainedLabels A Set of trained labels. Only samples with a label inside this set will be used to train.
     * @param calculateDistance If true, the distance to the nearest cluster will be calculated and appended to the sample metrics using the distance prefix @link {@link ClusterConstants#DISTANCE_PREFIX}
     */
    public MOASphereClusterer(T clusterer, Set<String> trainedLabels, boolean calculateDistance) {
        super(clusterer, trainedLabels, calculateDistance);
    }

    /**
     *
     * @param instance
     * @param clustering
     * @return
     * @throws IOException
     */
    @Override
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

    /**
     *
     * @return
     */
    @Override
    protected Clustering getClusteringResult() {
        return clusterer.getClusteringResult();
    }

    /**
     *

    @Override
    protected void printClustererParameters() {

    }
*/
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
}
