package metrics.algorithms.clustering;

import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.SampleConverger;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 *  This abstract class can be extended to transform clusterers from the MOA framework to algorithms.
 * @author fschmidt, mbyfield
 */
public abstract class MOAStreamClusterer<T extends AbstractClusterer & Serializable> extends AbstractAlgorithm {

    protected final T clusterer;
    protected long sampleCount;

    protected final SampleConverger converger = new SampleConverger(); // No predefined expected header

    // Only Samples with a label that is inside this set will be used for training the clusterer.
    // Other Samples will only query the clusterer, without changing the underlying model.
    // If this is null (by default), every Sample with a correct label is trained.
    protected Set<String> trainedLabels = null;

    // If this is true and trainedLabels is null, even Samples without a valid label will be used for training.\
    // Can be enabled using alwaysTrain().
    protected boolean trainUnknownLabels = false;

    // The clustering result, recalculated after each sample
    protected Clustering clusteringResult;

    public MOAStreamClusterer(T clusterer) {
        this.clusterer = clusterer;
    }

    public MOAStreamClusterer trainedLabels(Set<String> trainedLabels) {
        this.trainedLabels = trainedLabels;
        return this;
    }

    public MOAStreamClusterer alwaysTrain() {
        this.trainedLabels = null;
        trainUnknownLabels = true;
        return this;
    }

    /**
     * Executes the sample. This algorithm will train an underlying clusterer with the provided samples.
     * Depending on the settings, the algorithm will train all samples or all samples that have a given label.
     * The assigned ClusterId will be available from the {@link ClusterConstants#CLUSTER_TAG}.
     * A cluster id of -1 indicates noise. If distances are calculated, they will be appended to the metrics.
     * In the header, these fields will be named like the original field with the prefix: {@link ClusterConstants#DISTANCE_PREFIX}.
     * @param sample the executed sample
     * @return the sample with additional tags and metrics
     * @throws IOException if an error occurs
     */
    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        //TODO: added support for outlier detection requires refactoring and partial split in SphereClusterer and OutlierDetector (use subclass hook)
        sampleCount++;
        if (converger.getExpectedHeader() == null) {
            initalizeClusterer(sample);
        }

        // Handle changing headers
        // TODO: get rid of the converger, or make optional.
        double values[] = converger.getValues(sample);
        Header expectedHeader = converger.getExpectedHeader();

        // Transform sample to moa instance
        boolean hasLabel = sample.hasLabel();
        String label = sample.getLabel();
        String moaLabel = hasLabel ? label : ClusterConstants.UNKNOWN_LABEL;
        // TODO a new Instances object is created for every sample. Is this correct?
        // TODO do we even need the label here, since we are only clustering, not classifying?
        Instances instances = createInstances(expectedHeader, moaLabel);
        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(values, moaLabel, instances);

        // Check if sample should be used for training
        if ((trainedLabels != null && hasLabel && trainedLabels.contains(label)) ||
            (trainedLabels == null && (hasLabel || trainUnknownLabels))) {
            trainSample(sample, instance);
        }
        int bestFitCluster = calculateCluster(instance);
        sample = sampleClustered(sample, instance, bestFitCluster);
        sample.setTag(ClusterConstants.CLUSTER_TAG, Integer.toString(bestFitCluster));
        return sample;
    }

    /**
     * This method initializes the moa-clusterer. This method is used to set certain parameters,
     * that depend on the samples.
     * @param firstSample The first sample
     */
    protected void initalizeClusterer(Sample firstSample) {
        this.setupClustererParameter(firstSample);
        this.clusterer.resetLearning();
        // this.printClustererParameters();
        this.clusteringResult = null;
    }

    /**
     * This method can be used by implementing classes to do some additional processing after the best cluster has been calculated (e.g. calculate and append distance).
     * @param sample The current sample
     * @param instance The matching {@link com.yahoo.labs.samoa.instances.Instance} for the sample
     * @param bestFitCluster The id of the best fitting cluster
     */
    protected Sample sampleClustered(Sample sample, com.yahoo.labs.samoa.instances.Instance instance, int bestFitCluster) {
        // No changes to the sample by default.
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
        this.clusteringResult = null;
        try {
            this.clusteringResult = this.getClusteringResult();
        } catch (ArrayIndexOutOfBoundsException e){
            //mark sample as buffered (will have -1 cluster although used for learning)
            System.err.println("WARNING: Sample was trained, but no clustering result is available: " + e);
        }
    }

    /**
     * resets the learning
     */
    public synchronized void resetClusters() {
        clusterer.resetLearning();
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
        Attribute attr = new Attribute("class", new ArrayList<>(Collections.singletonList(label)));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
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
     * Calculates the best matching cluster for the current sample
     * @param instance the instance for the current sample
     * @return the id of the best matching cluster or -1 for noise
     */
    protected abstract int calculateCluster(com.yahoo.labs.samoa.instances.Instance instance);

}
