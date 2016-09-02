package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * This labeling algorithm exchanges the current label of each sample with the best label as calculated by the last clustering algorithm.
 * Requires the {@link ClusterConstants#CLUSTER_TAG} to be set correctly. It extends the metrics with the inclusion probability to given
 * labels.
 *
 * @author mbyfield, fschmidt
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    /**
     * Object to count occurences of labels in each cluster
     */
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

    public ClusterLabelingAlgorithm() {

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
        if (sample.getTag(ClusterConstants.IGNORE_SAMPLE) != null) return sample;
        int labelClusterId = sample.getClusterId();
        String originalLabel = sample.getLabel();
        if (originalLabel != null && labelClusterId >= 0) {
            clusterCounter.increment(labelClusterId, originalLabel);
        }

        boolean isBuffered = sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) != null;
        String newLabel = isBuffered ? ClusterConstants.BUFFERED_LABEL : clusterCounter.calculateLabel(labelClusterId);

        if (includeProbabilities && !isBuffered) {
            // New header
            Collection<String> ls = clusterCounter.getAllLabels();
            String allAnomalies[] = ls.toArray(new String[ls.size()]);
            String anomalyProbMetrics[] = new String[allAnomalies.length];

            // New metrics
            double[] anomalyProbs = new double[allAnomalies.length];
            for (int i = 0; i < allAnomalies.length; i++) {
                String anomaly = allAnomalies[i];
                anomalyProbMetrics[i] = ClusterConstants.INC_PROB_PREFIX + anomaly;
                Double inclusionProbability = clusterCounter.getLabelInclusionProbability(labelClusterId).get(anomaly);

                if (inclusionProbability == null)
                    anomalyProbs[i] = 0;
                else
                    anomalyProbs[i] = inclusionProbability;
            }

            if (stripData) {
                sample = new Sample(new Header(anomalyProbMetrics, sample.getHeader()), anomalyProbs, sample);
            } else {
                sample = sample.extend(anomalyProbMetrics, anomalyProbs);
            }
        }
        sample.setLabel(newLabel);
        if (originalLabel != null)
            sample.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        return sample;
    }

    public synchronized void resetCounters() {
        clusterCounter.reset();
    }

    @Override
    public String toString() {
        return "cluster labeling algorithm";
    }
}
