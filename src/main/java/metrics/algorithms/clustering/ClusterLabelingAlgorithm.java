package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static metrics.algorithms.clustering.ClusterConstants.INC_PROB_PREFIX;
import static org.apache.commons.lang3.ArrayUtils.toArray;

/**
 * This labeling algorithm exchanges the current label of each sample with the best label as calculated by the last clustering algorithm.
 * Requires the {@link ClusterConstants#CLUSTER_TAG} to be set correctly. It extends the metrics with the inclusion probability to given labels.
 * @author mbyfield, fschmidt
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    /** Object to count occurences of labels in each cluster */
    protected ClusterCounter clusterCounter;

    /**
     * Should the probabilities be appended as metrics?
     */
    protected boolean includeProbabilities = false;

    /**
     * Strip previously appended data? e.g. distances - depricated but no
     */
    protected boolean stripData = false;

    protected Set<String> trainedLabels = null;

    public static final double DEFAULT_THRESHOLD_TO_CLASSIFY = 0.0;
    //TODO do all the sanetizing etc

    public ClusterLabelingAlgorithm(){

    }

    public ClusterLabelingAlgorithm setIncludeProbabilities(boolean includeProbabilities) {
        this.includeProbabilities = includeProbabilities;
        return this;
    }

    public ClusterLabelingAlgorithm setStripData(boolean stripData) {
        this.stripData = stripData;
        return this;
    }

    public ClusterLabelingAlgorithm setTrainedLabels(Set<String> trainedLabels) {
        this.trainedLabels = trainedLabels;
        return this;
    }

    public ClusterLabelingAlgorithm(double thresholdToClassify, boolean includeProbabilities, boolean stripData) {
        this(thresholdToClassify, includeProbabilities, stripData, null);
    }

    public ClusterLabelingAlgorithm(double thresholdToClassify, boolean includeProbabilities, boolean stripData, Set<String> trainedLabels) {
        this.clusterCounter = new ClusterCounter(thresholdToClassify);
        this.includeProbabilities = includeProbabilities;
        this.stripData = stripData;
        this.trainedLabels = trainedLabels;
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        int labelClusterId = sample.getClusterId();
        String originalLabel = sample.getLabel();
        if (originalLabel != null && labelClusterId >= 0 ) //&& (trainedLabels == null || trainedLabels.contains(originalLabel)) && sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null)
            clusterCounter.increment(labelClusterId, originalLabel);
        String newLabel = sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null? clusterCounter.calculateLabel(labelClusterId) : ClusterConstants.BUFFERED_LABEL;

        Sample sampleToReturn;
        if (includeProbabilities && sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null) {
            // New header
            Collection<String> ls = clusterCounter.getAllLabels();
            String allAnomalies[] = ls.toArray(new String[ls.size()]);

            // New metrics
            double[] anomalyProbs = new double[allAnomalies.length];
            for (int i = 0; i < allAnomalies.length; i++) {
                String anomaly = allAnomalies[i];
                Double inclusionProbability = clusterCounter.getLabelInclusionProbability(labelClusterId).get(anomaly);
                if (inclusionProbability == null)
                    anomalyProbs[i] = 0;
                else
                    anomalyProbs[i] = inclusionProbability;
            }

//            for (int i = 0; i < allAnomalies.length; i++) {
//                if(allAnomalies[i] == null || allAnomalies[i].equalsIgnoreCase("null")){
//                    System.out.println("b");
//                }
//                allAnomalies[i] = INC_PROB_PREFIX + allAnomalies[i];
//            }TODO remove
            if (stripData) {
                sampleToReturn = new Sample(new Header(allAnomalies, sample.getHeader()), anomalyProbs, sample);
            } else {
                sampleToReturn = sample.extend(allAnomalies, anomalyProbs);
            }
        } else {
            if (stripData)
                sampleToReturn = new Sample(Header.EMPTY_HEADER, new double[0], sample);
            else
                sampleToReturn = new Sample(sample);
        }

        sampleToReturn.setLabel(newLabel);
        if (originalLabel != null)
            sampleToReturn.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        return sampleToReturn;
    }

    public synchronized void resetCounters() {
        clusterCounter.reset();
    }

    @Override
    public String toString() {
        return "cluster labeling algorithm";
    }
}
