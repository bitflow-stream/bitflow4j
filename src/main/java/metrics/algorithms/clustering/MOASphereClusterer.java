package metrics.algorithms.clustering;


import com.yahoo.labs.samoa.instances.Instance;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.DoubleStream;

public abstract class MOASphereClusterer<T extends AbstractClusterer & Serializable> extends MOAStreamClusterer<T> {

    public MOASphereClusterer(T clusterer, boolean alwaysTrain, boolean calculateDistance) {
        super(clusterer, alwaysTrain, calculateDistance);
    }

    public MOASphereClusterer(T clusterer, Set<String> trainedLabels, boolean calculateDistance) throws IllegalArgumentException {
        super(clusterer, trainedLabels, calculateDistance);
    }

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

    @Override
    protected Clustering getClusteringResult() {
//        if (clusterer instanceof WithDBSCAN || clusterer instanceof StreamKM) {
//            return clusterer.getClusteringResult();
//        } else {
//            return clusterer.getMicroClusteringResult();
//        }
        return clusterer.getMicroClusteringResult();
    }

    @Override
    protected void printClustererParameters() {
        com.github.javacliparser.Option[] options = this.clusterer.getOptions().getOptionArray();
        for (com.github.javacliparser.Option o : options) {
//            System.out.println(o.getPurpose() + " Type: " + o. + " Value: ");
            System.out.println(o.getDefaultCLIString());
        }
        //        if (this.clusterer instanceof WithDBSCAN) {
//            System.out.println("horizonOption: " + ((WithDBSCAN) this.clusterer).horizonOption.getValue());
//            System.out.println("initPointsOption: " + ((WithDBSCAN) this.clusterer).initPointsOption.getValue());
//            System.out.println("speedOption: " + ((WithDBSCAN) this.clusterer).speedOption.getValue());
//            System.out.println("betaOption: " + ((WithDBSCAN) this.clusterer).betaOption.getValue());
//            System.out.println("lambdaOption: " + ((WithDBSCAN) this.clusterer).lambdaOption.getValue());
//            System.out.println("epsilonOption: " + ((WithDBSCAN) this.clusterer).epsilonOption.getValue());
//            System.out.println("muOption: " + ((WithDBSCAN) this.clusterer).muOption.getValue());
//            System.out.println("offlineOption: " + ((WithDBSCAN) this.clusterer).offlineOption.getValue());
//        } else if (this.clusterer instanceof Clustream) {
//            System.out.println("timeWindowOption: " + ((Clustream) this.clusterer).timeWindowOption.getValue());
//            System.out.println("maxNumKernelsOption: " + ((Clustream) this.clusterer).maxNumKernelsOption.getValue());
//            System.out.println("kernelRadiFactorOption: " + ((Clustream) this.clusterer).kernelRadiFactorOption.getValue());
//        } else if (this.clusterer instanceof ClusTree) {
//            System.out.println("horizonOption: " + ((ClusTree) this.clusterer).horizonOption.getValue());
//            System.out.println("maxHeightOption: " + ((ClusTree) this.clusterer).maxHeightOption.getValue());
//        } else if (this.clusterer instanceof BICO) {
//            System.out.println("numClustersOption: " + ((BICO) this.clusterer).numClustersOption.getValue());
//            System.out.println("maxNumClusterFeaturesOption: " + ((BICO) this.clusterer).maxNumClusterFeaturesOption.getValue());
//            System.out.println("numDimensionsOption: " + ((BICO) this.clusterer).numDimensionsOption.getValue());
//            System.out.println("numProjectionsOption: " + ((BICO) this.clusterer).numProjectionsOption.getValue());
//        } else if (this.clusterer instanceof StreamKM) {
//            System.out.println("sizeCoresetOption: " + ((StreamKM) this.clusterer).sizeCoresetOption.getValue());
//            System.out.println("numClustersOption: " + ((StreamKM) this.clusterer).numClustersOption.getValue());
//            System.out.println("widthOption: " + ((StreamKM) this.clusterer).widthOption.getValue());
//            System.out.println("randomSeedOption: " + ((StreamKM) this.clusterer).randomSeedOption.getValue());
//        }
    }

    protected Map.Entry<Double, double[]> distance(double[] center, double[] doubles, double radius) {
        if (center.length != doubles.length || center.length == 0) {
            throw new IllegalStateException("THIS SHOULD NOT HAPPEN: " + center.length + " vs " + doubles.length);
        }
        double distanceCenter;
        Double distance;
        double[] distances = new double[center.length - 1];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Math.abs(center[i] - doubles[i]);
//            distances[i] = center[i] - doubles[i];
        }
        distanceCenter = Math.sqrt(DoubleStream.of(distances).map(dist -> dist * dist).sum());
        distance = distanceCenter - radius;
        double factor = distance / distanceCenter;
        for (int i = 0; i < distances.length; i++) {
            distances[i] = distances[i] * factor;
        }
        return new TreeMap.SimpleEntry<>(distance, distances);
    }

}
