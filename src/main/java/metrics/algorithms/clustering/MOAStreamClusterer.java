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
 *  This abstract class can be extended to transform clusterers from the MOA framework to algorithms.
 * @author fschmidt, mbyfield
 */
public abstract class MOAStreamClusterer<T extends AbstractClusterer & Serializable> extends AbstractAlgorithm {

    //The moa clusterer
    protected final T clusterer;
    protected long sampleCount;

    protected final SampleConverger converger = new SampleConverger(); // No predefined expected header

    //train on all labels
    protected final boolean alwaysTrain;

    //indicates wether the buffering phase is finished and correct results can be fetched
//    protected boolean buffering;

    //set of trained labels
    protected final Set<String> trainedLabels;

    protected boolean calculateDistance;
//    protected Clustering clusteringResult;

    //The clustering result, recalculated after each sample
    protected volatile Clustering clusteringResult;

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
    }

    /**
     * This method initialized the moa-clusterer. This method is used to set certain parameters, that depend on the samples (e.g.
     * @param firstSample The first sample
     */
    protected void initalizeClusterer(Sample firstSample) {
        //
        this.setupClustererParameter(firstSample);
        this.clusterer.resetLearning();
        //this.printClustererParameters();
        this.clusteringResult = null;
    }

    /**
     * Executes the sample. This algorithm will train an underlying clusterer with the provided samples.
     * Depending on the settings, the algorith will train all samples or all samples that have a given label.
     * The assigned ClusterId will be available from the {@link ClusterConstants#CLUSTER_TAG}.
     * A cluster id of -1 indicates noice. If distances are calculated, they will be appended to the metrics.
     * In the header, these fields will be named like the original field with the prefix: {@link ClusterConstants#DISTANCE_PREFIX}.
     * @param sample the executed sample
     * @return the sample with additional tags and metrics
     * @throws IOException if an error occurs
     */
    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        //TODO: added support for outlier detection requires refactoring and partial split in SphereClusterer and OutlierDetector (use subclass hook)

        sampleCount++;
        int bestFitCluster;
        Map.Entry<Double, double[]> distance;
        // make sure all samples have a label
        String label = getLabel(sample);
        //initialize clusterer with a header an dimensions
        if (converger.getExpectedHeader() == null) {
            initalizeClusterer(sample);
        }
        // handle changing headers
        double values[] = converger.getValues(sample);
        Header expectedHeader = converger.getExpectedHeader();
        //transform sample to moa instance
        Instances instances = createInstances(expectedHeader, label);
        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(values, label, instances);

        //check if sample should be trained
        if (alwaysTrain || (label != null && !label.isEmpty() && (trainedLabels == null || trainedLabels.contains(label)))) {
            trainSample(sample, instance);
        }
//        ClusteringResult clusteringResultObject = new ClusteringResult(instance).invoke();
//        int bestFitCluster = clusteringResultObject.getBestFitCluster();
//        distance = clusteringResultObject.getDistances();
        //TODO: we need a valid mechanism to identify the end of the bufferphase
        bestFitCluster = calculateCluster(instance);
        distance = calculateDistances(instance, bestFitCluster);
        //mark samples if they executed during buffering phase (no result vailable)
        setBufferingStatus(sample);
        sample.setTag(ClusterConstants.CLUSTER_TAG, Integer.toString(bestFitCluster));
        sample = appendDistance(sample, distance);
        return sample;
    }

    private Sample appendDistance(Sample sample, Map.Entry<Double, double[]> distance) {
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
     * Trains a the clusterer with the current sample
     * @param sample The current sample
     * @param instance the matching instance for the sample
     */
    private void trainSample(Sample sample, com.yahoo.labs.samoa.instances.Instance instance) {
        //TODO: we use trainOnInstanceImpl() for both outlier detection algorithms and clustering algorithms. This is consistent with the current implementation of the outlier detection algorithms, but the interface moa.clusterers.outliers.MyBaseOutlierDetector suggests using processNewInstanceImpl
        clusterer.trainOnInstance(instance);
        //check if clusterer finished buffering
        try{
            this.clusteringResult = this.getClusteringResult();
        }catch (ArrayIndexOutOfBoundsException e){
            //mark sample as buffered (will have -1 cluster although used for learning)
            //TODO: find a way to mark later?
//            sample.setTag(ClusterConstants.BUFFERED_SAMPLE_TAG, "1");
//            System.out.println("WARNING: Sample was trained, but no clustering result is available (this may happen during the bufferphase)");
        }
        //mark sample as trained sample
        sample.setTag(ClusterConstants.TRAINING_TAG, "1");
    }

    /**
     * resets the learning
     */
    public synchronized void resetClusters() {
        clusterer.resetLearning();
    }

    /**
     * Get a label for the current sample.
     * @param sample the current sample
     * @return the samples current label or {@link ClusterConstants#UNKNOWN_LABEL} if no label was set.
     */
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

    /**
     * Uses a previously created {@link Instances} object and the current value array to create an {com.yahoo.labs.samoa.instances.Instance} that can be used by the moa clusterer.
     * @param values the values of the current sample.
     * @param label the label of the current sample
     * @param instances an {@link Instances} object. Can be obtained using the {@link MOAStreamClusterer#createInstances(Header, String)} method.
     * @return
     */
    protected com.yahoo.labs.samoa.instances.Instance makeInstance(double values[], String label, Instances instances) {
        //TODO: refactor all of this stuff to a saperate class
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(instances);
        instance.setClassValue(label);
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        return converter.samoaInstance(instance);
    }

    /**
     * Creates an {@link Instances} object that can be used for the {@link MOAStreamClusterer#makeInstance(double[], String, Instances)} method.
     * @param header The header of the sample
     * @param label The label of the sample
     * @return An {@link Instances} object
     */
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

    /**
     * Adds the current label to the sorted set of all labels
     * @param label the current label
     * @return An @link{java.util.ArrayList} containing all labels
     */
    protected ArrayList<String> allClasses(String label) {
        // TODO is this necessary?
        //TODO: looks strange, Set is never saved, why copy it and why create a single valued set, might be a bug or obsolete artifact
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
     * @return an instance of @link{moa.cluster.Clustering}
     */
    protected abstract Clustering getClusteringResult();

    /**
     *
     * @param instance the evaluated instance.
     * @param clustering The clustering used for evaluation
     * @return a @link{java.util.Map.Entry} with the overall distance as the key and the distances per dimension inside the array.
     * @throws IOException when an error occurs TODO: add custome exception
     */
    protected abstract Map.Entry<Double, double[]> getDistance(com.yahoo.labs.samoa.instances.Instance instance, Clustering clustering) throws IOException;

    /**
     * Calculates the best matching cluster for the current sample
     * @param instance the instance for the current sample
     * @return the id of the best matching cluster or -1 for noise
     */
    protected abstract int calculateCluster(com.yahoo.labs.samoa.instances.Instance instance);

    /**
     * Calculate the distance to the nearest real cluster for samples in the noise cluster
     * @param instance the instance in the cluster
     * @param bestFitCluster the id of the best fit cluster
     * @return  A {@link java.util.Map.Entry} containing the overall distance to the closest cluster and the an array holding the distances for each dimension.
     */
    private Map.Entry<Double, double[]> calculateDistances(com.yahoo.labs.samoa.instances.Instance instance, int bestFitCluster){
        if (clusteringResult != null && calculateDistance && bestFitCluster == -1) {
            try {
                return getDistance(instance, clusteringResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }return null;
    }

    /**
     * If no clustering result is available, a {@link ClusterConstants#BUFFERED_SAMPLE_TAG} tag will be added.
     * @param sample the sample
     */
    private void setBufferingStatus(Sample sample) {
        if(clusteringResult == null) sample.setTag(ClusterConstants.BUFFERED_SAMPLE_TAG, "1");
    }
}
