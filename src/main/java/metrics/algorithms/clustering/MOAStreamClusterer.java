package metrics.algorithms.clustering;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.clustream.Clustream;
import moa.clusterers.clustree.ClusTree;
import moa.clusterers.denstream.WithDBSCAN;
import moa.clusterers.kmeanspm.BICO;
import moa.clusterers.streamkm.StreamKM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author fschmidt
 */
public class MOAStreamClusterer<T extends AbstractClusterer & Serializable> extends AbstractAlgorithm {

    private final T clusterer;
    private final Map<Integer, Map<String, Integer>> clusterLabelMaps;
    private int sampleCount = 0;
    private int printClusterDetails = 0;

    public MOAStreamClusterer(T clusterer, int printClusterDetails) {
        this.clusterer = clusterer;
        this.setupClustererParameter();

        this.clusterer.resetLearning();

        this.printClustererParameters();

        this.clusterLabelMaps = new HashMap<>();
        this.printClusterDetails = printClusterDetails;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        Instances instances = createInstances(sample);
        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(sample, instances);

        clusterer.trainOnInstance(instance);

        //Testing specific output parameters for DenStream clusterer
        if (clusterer instanceof WithDBSCAN || clusterer instanceof StreamKM) {

            //prints all micro-clusters
            double inclusionProbability = 0.0;
            int bestFitCluster = -1;
            int clusterNum = 0;
            for (moa.cluster.Cluster c : this.clusterer.getClusteringResult().getClustering()) {
                double clusterInclusionProbability = c.getInclusionProbability(instance);
                if (inclusionProbability < clusterInclusionProbability) {
                    inclusionProbability = clusterInclusionProbability;
                    bestFitCluster = clusterNum;
                }
                clusterNum++;
            }

            //Evaluate Clusters
            if (clusterLabelMaps.containsKey(bestFitCluster)) {
                Map<String, Integer> labelCountMap = clusterLabelMaps.get(bestFitCluster);
                if (labelCountMap.containsKey(sample.getLabel())) {
                    labelCountMap.put(sample.getLabel(), labelCountMap.get(sample.getLabel()) + 1);
                } else {
                    labelCountMap.put(sample.getLabel(), 1);
                }
            } else {
                Map<String, Integer> labelCountMap = new HashMap<>();
                labelCountMap.put(sample.getLabel(), 1);
                clusterLabelMaps.put(bestFitCluster, labelCountMap);
            }
            //Print Evaluation
            if (printClusterDetails > 0) {
                if (sampleCount % printClusterDetails == 0) {
                    System.out.println("##########MAP-COUNT##############");
                    List<Integer> clusterIds = new ArrayList<>(clusterLabelMaps.keySet());
                    Collections.sort(clusterIds);

                    for (Integer clusterId : clusterIds) {
                        System.out.println("----------------------");
                        System.out.println("cluster id: " + clusterId);
                        Map<String, Integer> scenarioCount = clusterLabelMaps.get(clusterId);
                        Stream<Map.Entry<String, Integer>> sorted = scenarioCount.entrySet().stream().sorted(Collections.reverseOrder(
                                Map.Entry
                                .comparingByValue()));
                        sorted.forEach(System.out::println);
                    }
                    System.out.println("##########END##############");
                    System.out.println("Sample: " + Arrays.toString(sample.getMetrics()));
                }
                sampleCount++;
            }

            String label = "Cluster-" + clusterNum;
            return new Sample(sample.getHeader(), sample.getMetrics(),
                    sample.getTimestamp(), sample.getSource(), label);

        }else{
            //prints all micro-clusters
            double inclusionProbability = 0.0;
            int bestFitCluster = -1;
            int clusterNum = 0;
            for (moa.cluster.Cluster c : clusterer.getMicroClusteringResult().getClustering()) {
                double clusterInclusionProbability = c.getInclusionProbability(instance);
                if (inclusionProbability < clusterInclusionProbability) {
                    inclusionProbability = clusterInclusionProbability;
                    bestFitCluster = clusterNum;
                }
                clusterNum++;
            }

            //Evaluate Clusters
            if (clusterLabelMaps.containsKey(bestFitCluster)) {
                Map<String, Integer> labelCountMap = clusterLabelMaps.get(bestFitCluster);
                if (labelCountMap.containsKey(sample.getLabel())) {
                    labelCountMap.put(sample.getLabel(), labelCountMap.get(sample.getLabel()) + 1);
                } else {
                    labelCountMap.put(sample.getLabel(), 1);
                }
            } else {
                Map<String, Integer> labelCountMap = new HashMap<>();
                labelCountMap.put(sample.getLabel(), 1);
                clusterLabelMaps.put(bestFitCluster, labelCountMap);
            }
            //Print Evaluation
            if (printClusterDetails > 0) {
                if (sampleCount % printClusterDetails == 0) {
                    System.out.println("##########MAP-COUNT##############");
                    List<Integer> clusterIds = new ArrayList<>(clusterLabelMaps.keySet());
                    Collections.sort(clusterIds);

                    for (Integer clusterId : clusterIds) {
                        System.out.println("----------------------");
                        System.out.println("cluster id: " + clusterId);
                        Map<String, Integer> scenarioCount = clusterLabelMaps.get(clusterId);
                        Stream<Map.Entry<String, Integer>> sorted = scenarioCount.entrySet().stream().sorted(Collections.reverseOrder(
                                Map.Entry
                                .comparingByValue()));
                        sorted.forEach(System.out::println);
                    }
                    System.out.println("##########END##############");
                    System.out.println("Sample: " + Arrays.toString(sample.getMetrics()));
                }
                sampleCount++;
            }

            String label = "Cluster-" + clusterNum;
            return new Sample(sample.getHeader(), sample.getMetrics(),
                    sample.getTimestamp(), sample.getSource(), label);
        }

    }

    @Override
    public String toString() {
        return "moa clusterer";
    }

    private com.yahoo.labs.samoa.instances.Instance makeInstance(Sample sample, Instances instances) {
        double[] values = sample.getMetrics();
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(instances);
        instance.setClassValue(sample.getLabel());

        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        com.yahoo.labs.samoa.instances.Instance samoaInstance = converter.samoaInstance(instance);
        return samoaInstance;
    }

    private Instances createInstances(Sample sample) {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), 0);
        for (String field : sample.getHeader().header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allClasses(sample));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    private ArrayList<String> allClasses(Sample sample) {
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        allLabels.add(sample.getLabel());
        return new ArrayList<>(allLabels);
    }
    
    private void printClustererParameters(){
        //Sysout Parameter
        if (this.clusterer instanceof WithDBSCAN) {
            System.out.println("horizonOption: " + ((WithDBSCAN) this.clusterer).horizonOption.getValue());
            System.out.println("initPointsOption: " + ((WithDBSCAN) this.clusterer).initPointsOption.getValue());
            System.out.println("speedOption: " + ((WithDBSCAN) this.clusterer).speedOption.getValue());
            System.out.println("betaOption: " + ((WithDBSCAN) this.clusterer).betaOption.getValue());
            System.out.println("lambdaOption: " + ((WithDBSCAN) this.clusterer).lambdaOption.getValue());
            System.out.println("epsilonOption: " + ((WithDBSCAN) this.clusterer).epsilonOption.getValue());
            System.out.println("muOption: " + ((WithDBSCAN) this.clusterer).muOption.getValue());
            System.out.println("offlineOption: " + ((WithDBSCAN) this.clusterer).offlineOption.getValue());
        } else if (this.clusterer instanceof Clustream) {
            System.out.println("timeWindowOption: " + ((Clustream) this.clusterer).timeWindowOption.getValue());
            System.out.println("maxNumKernelsOption: " + ((Clustream) this.clusterer).maxNumKernelsOption.getValue());
            System.out.println("kernelRadiFactorOption: " + ((Clustream) this.clusterer).kernelRadiFactorOption.getValue());
        } else if (this.clusterer instanceof ClusTree) {
            System.out.println("horizonOption: " + ((ClusTree) this.clusterer).horizonOption.getValue());
            System.out.println("maxHeightOption: " + ((ClusTree) this.clusterer).maxHeightOption.getValue());
        } else if (this.clusterer instanceof BICO) {
            System.out.println("numClustersOption: " + ((BICO) this.clusterer).numClustersOption.getValue());
            System.out.println("maxNumClusterFeaturesOption: " + ((BICO) this.clusterer).maxNumClusterFeaturesOption.getValue());
            System.out.println("numDimensionsOption: " + ((BICO) this.clusterer).numDimensionsOption.getValue());
            System.out.println("numProjectionsOption: " + ((BICO) this.clusterer).numProjectionsOption.getValue());
        } else if (this.clusterer instanceof StreamKM) {
            System.out.println("sizeCoresetOption: " + ((StreamKM) this.clusterer).sizeCoresetOption.getValue());
            System.out.println("numClustersOption: " + ((StreamKM) this.clusterer).numClustersOption.getValue());
            System.out.println("widthOption: " + ((StreamKM) this.clusterer).widthOption.getValue());
            System.out.println("randomSeedOption: " + ((StreamKM) this.clusterer).randomSeedOption.getValue());
        }
    }
    
    private void setupClustererParameter(){
        //Testing specific parameters for DenStream clusterer
        if (this.clusterer instanceof WithDBSCAN) {
            IntOption horizonOption = new IntOption("horizon", 'h',
                    "Range of the window.", 1000);
            IntOption initPointsOption = new IntOption("initPoints", 'i',
                    "Number of points to use for initialization.", 1000);
            IntOption speedOption = new IntOption("processingSpeed", 's',
                    "Number of incoming points per time unit.", 100, 1, 1000);
            FloatOption betaOption = new FloatOption("beta", 'b', "", 0.2, 0,
                    1);
            FloatOption epsilonOption = new FloatOption("epsilon", 'e',
                    "Defines the epsilon neighbourhood", 0.15, 0, 1);
            FloatOption lambdaOption = new FloatOption("lambda", 'l', "",
                    0.25, 0, 1);
            FloatOption muOption = new FloatOption("mu", 'm', "", 1, 0,
                    Double.MAX_VALUE);
            FloatOption offlineOption = new FloatOption("offline", 'o',
                    "offline multiplier for epsilion.", 2, 2, 20);
            ((WithDBSCAN) this.clusterer).horizonOption = horizonOption;
            ((WithDBSCAN) this.clusterer).initPointsOption = initPointsOption;
            ((WithDBSCAN) this.clusterer).speedOption = speedOption;
            ((WithDBSCAN) this.clusterer).betaOption = betaOption;
            ((WithDBSCAN) this.clusterer).lambdaOption = lambdaOption;
            ((WithDBSCAN) this.clusterer).epsilonOption = epsilonOption;
            ((WithDBSCAN) this.clusterer).muOption = muOption;
            ((WithDBSCAN) this.clusterer).offlineOption = offlineOption;

        } else if (this.clusterer instanceof Clustream) {
            IntOption timeWindowOption = new IntOption("horizon",
                    'h', "Rang of the window.", 1000);
            IntOption maxNumKernelsOption = new IntOption(
                    "maxNumKernels", 'k',
                    "Maximum number of micro kernels to use.", 100);
            IntOption kernelRadiFactorOption = new IntOption(
                    "kernelRadiFactor", 't',
                    "Multiplier for the kernel radius", 2);
            ((Clustream) this.clusterer).timeWindowOption = timeWindowOption;
            ((Clustream) this.clusterer).maxNumKernelsOption = maxNumKernelsOption;
            ((Clustream) this.clusterer).kernelRadiFactorOption = kernelRadiFactorOption;
        } else if (this.clusterer instanceof ClusTree) {
            IntOption horizonOption = new IntOption("horizon",
                    'h', "Range of the window.", 1000);
            IntOption maxHeightOption = new IntOption(
                    "maxHeight", 'H',
                    "The maximal height of the tree", 8);

            ((ClusTree) this.clusterer).horizonOption = horizonOption;
            ((ClusTree) this.clusterer).maxHeightOption = maxHeightOption;
        } else if (this.clusterer instanceof BICO) {
            IntOption numClustersOption = new IntOption("Cluster", 'k',
                    "Number of desired centers.", 100, 1, Integer.MAX_VALUE);
            IntOption numDimensionsOption = new IntOption("Dimensions", 'd',
                    "Number of the dimensions of the input points.", 122, 1,
                    Integer.MAX_VALUE);
            IntOption maxNumClusterFeaturesOption = new IntOption(
                    "MaxClusterFeatures", 'n', "Maximum size of the coreset.", 5 * 250, 1,
                    Integer.MAX_VALUE);
            IntOption numProjectionsOption = new IntOption("Projections", 'p',
                    "Number of random projections used for the nearest neighbour search.",
                    1, 1, Integer.MAX_VALUE);

            ((BICO) this.clusterer).numClustersOption = numClustersOption;
            ((BICO) this.clusterer).maxNumClusterFeaturesOption = maxNumClusterFeaturesOption;
            ((BICO) this.clusterer).numDimensionsOption = numDimensionsOption;
            ((BICO) this.clusterer).numProjectionsOption = numProjectionsOption;
        } else if (this.clusterer instanceof StreamKM) {
            IntOption sizeCoresetOption = new IntOption("sizeCoreset",
                    's', "Size of the coreset.", 1000);
            IntOption numClustersOption = new IntOption(
                    "numClusters", 'k',
                    "Number of clusters to compute.", 5);
            IntOption widthOption = new IntOption("width",
                    'w', "Size of Window for training learner.", 10000, 0, Integer.MAX_VALUE);
            IntOption randomSeedOption = new IntOption("randomSeed", 'r',
                    "Seed for random behaviour of the classifier.", 1);

            ((StreamKM) this.clusterer).sizeCoresetOption = sizeCoresetOption;
            ((StreamKM) this.clusterer).numClustersOption = numClustersOption;
            ((StreamKM) this.clusterer).widthOption = widthOption;
            ((StreamKM) this.clusterer).randomSeedOption = randomSeedOption;
        }
    }
}