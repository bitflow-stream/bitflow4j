package metrics.algorithms.clustering;

import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.SampleConverger;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fschmidt, mbyfield
 */
public abstract class MOAStreamClusterer<T extends AbstractClusterer & Serializable> extends AbstractAlgorithm {

    //The moa clusterer
    protected final T clusterer;

    protected final SampleConverger converger = new SampleConverger(); // No predefined expected header

    //train on all labels
    protected final boolean alwaysTrain;

    //set of trained labels
    protected final Set<String> trainedLabels;

    protected boolean calculateDistance;
//    protected Clustering clusteringResult;

    //The clustering result, recalculated after each sample
    protected volatile Clustering clusteringResult;

    //number of trained samples
    private int count;
    //number of correct predictions
    private int correct;
    //number of wrong predictions
    private int wrong;

    /**
     * This constructor can be used to train on all labels or on the default label ("idle").
     * @param clusterer The moa clusterer
     * @param alwaysTrain If true, all labels are used for training, else only the "idle" lable is trained
     * @param calculateDistance If true, calculate the Distance for non matching samples and append the result
     */
    public MOAStreamClusterer(T clusterer, boolean alwaysTrain, boolean calculateDistance) {
        this.clusterer = clusterer;
        this.alwaysTrain = alwaysTrain;
        this.trainedLabels = alwaysTrain ? null : new HashSet<>(Arrays.asList(new String[]{"idle"}));
        this.calculateDistance = calculateDistance;
    }

    // Train cluster only for certain labels (or give null-set to train on all VALID labels)

    /**
     * This constructor can be used to train on a given set of labels.
     * @param clusterer The moa clusterer
     * @param trainedLabels The set of trained labels, if null, all valid labels are trained
     * @param calculateDistance If true, calculate the Distance for non matching samples and append the result
     */
    public MOAStreamClusterer(T clusterer, Set<String> trainedLabels, boolean calculateDistance) {
        this.clusterer = clusterer;
        this.alwaysTrain = false;
        this.trainedLabels = trainedLabels;
        this.calculateDistance = calculateDistance;
//        clusteringResult = null;
    }

    /**
     * This method initialized the clusterer
     * @param firstSample
     */
    protected void initalizeClusterer(Sample firstSample) {
        this.setupClustererParameter(firstSample);
        this.clusterer.resetLearning();
        this.printClustererParameters();
        this.clusteringResult = null;
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        if (converger.getExpectedHeader() == null) {
            initalizeClusterer(sample);
        }
        String label = getLabel(sample);
        double values[] = converger.getValues(sample);

        Header expectedHeader = converger.getExpectedHeader();
        Instances instances = createInstances(expectedHeader, label);
        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(values, label, instances);

        boolean trained = false;
        boolean matchingClusterFound = false;
        if (alwaysTrain || (label != null && !label.isEmpty() && (trainedLabels == null || trainedLabels.contains(label)))) {
            clusterer.trainOnInstance(instance);
            trained = true;
            if (count > 205)
            this.clusteringResult = this.getClusteringResult();

            count ++;
            matchingClusterFound = true;
        } else if(clusteringResult != null){
            matchingClusterFound = clusteringResult.getMaxInclusionProbability(instance) != 0;
        }

        Map.Entry<Double, double[]> distance = null;
        double inclusionProbability = 0.0;
        int bestFitCluster = -1;
        int clusterNum = 0;
        if (clusteringResult != null) {
            AutoExpandVector<Cluster> clustering = clusteringResult.getClustering();
            if (matchingClusterFound) {
                for (Cluster c : clustering) {
                    double clusterInclusionProbability = c.getInclusionProbability(instance);
                    if (inclusionProbability < clusterInclusionProbability) {
                        inclusionProbability = clusterInclusionProbability;
                        bestFitCluster = clusterNum;
                    }
                    clusterNum++;
                }
            } else if (calculateDistance) {
                distance = getDistance(instance, clusteringResult);
            }
        }
            sample.setTag(ClusterConstants.CLUSTER_TAG, Integer.toString(bestFitCluster));
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
//        System.out.println(bestFitCluster);
//        System.out.println((bestFitCluster == -1 && label.equals("idle")) || (bestFitCluster != -1 && label.equals("idle")));
        if ((bestFitCluster == -1 && label.equals("idle")) || (bestFitCluster != -1 && label.equals("idle"))){
            correct++;
        }else{
            wrong++;
        }
        if((wrong + correct) % 100 == 0) System.out.println("wrong: " + wrong + " ; correct: " + correct);
        return sample;
    }

    public synchronized void resetClusters() {
        clusterer.resetLearning();
    }

    protected String getLabel(Sample sample) {
        String label = sample.getLabel();
        if (label == null || label.isEmpty()) {
            label = ClusterConstants.UNKNOWN_LABEL;
        }
        return label;
    }

    @Override
    public String toString() {
        return "moa clusterer";
    }

    protected com.yahoo.labs.samoa.instances.Instance makeInstance(double values[], String label, Instances instances) {
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(instances);
        instance.setClassValue(label);
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        return converter.samoaInstance(instance);
    }

    protected Instances createInstances(Header header, String label) {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), 0);
        for (String field : header.header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allClasses(label));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    protected ArrayList<String> allClasses(String label) {
        // TODO is this necessary?
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        allLabels.add(label);
        return new ArrayList<>(allLabels);
    }

    /**
     * This method will print the configuration details of the internal moa clusterer
     */
    protected void printClustererParameters(){
        com.github.javacliparser.Option[] options = this.clusterer.getOptions().getOptionArray();
        for (com.github.javacliparser.Option o : options) {
            System.out.println(o.getDefaultCLIString());
        }
    }

    /**
     * This method is called when the first sample is processed.
     * Subclasses can use this method to configure the clusterer with additional parameter (e.g. the number of dimension).
     * The method is called after the initial clusterer setup
     * @param firstSample The first sample processed by the algorithm
     */
    protected abstract void setupClustererParameter(Sample firstSample);

    /**
     * This method is called during the evaluation process for each executed sample.
     * Subclasses must provide the implementation and return a {@link Clustering}.
     * @return
     */
    protected abstract Clustering getClusteringResult();

    /**
     *
     * @param instance
     * @param clustering
     * @return
     * @throws IOException
     */
    protected abstract Map.Entry<Double, double[]> getDistance(com.yahoo.labs.samoa.instances.Instance instance, Clustering clustering) throws IOException;

}
