package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Collection;

/**
 * This labeling algorithm exchanges the current label of each sample with the best label as calculated by the last
 * clustering algorithm. Requires the {@link ClusterConstants#CLUSTER_TAG} to be set correctly. It extends the
 * metrics with the inclusion probability to given labels.
 *
 * @author mbyfield, fschmidt
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    /**
     * Object to count occurrences of labels in each cluster
     */
    protected ClusterCounter clusterCounter;

    /**
     * Should the probabilities be appended as metrics?
     */
    protected boolean includeProbabilities = false;

    public ClusterLabelingAlgorithm(double thresholdToClassify, boolean includeProbabilities) {
        this.clusterCounter = new ClusterCounter(thresholdToClassify);
        this.includeProbabilities = includeProbabilities;
    }

    public ClusterLabelingAlgorithm includeProbabilities() {
        this.includeProbabilities = true;
        return this;
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        int clusterId = sample.getClusterId();
        String originalLabel = sample.getLabel();
        if (originalLabel != null && clusterId >= 0) {
            clusterCounter.increment(clusterId, originalLabel);
        }
        String newLabel = clusterCounter.calculateLabel(clusterId);

        if (includeProbabilities) {
            sample = extendWithProbabilities(sample, clusterId);
        }
        sample.setLabel(newLabel);
        if (originalLabel != null)
            sample.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        return sample;
    }

    private Sample extendWithProbabilities(Sample sample, int clusterId) {
        // New header
        Collection<String> ls = clusterCounter.getAllLabels();
        String allAnomalies[] = ls.toArray(new String[ls.size()]);
        String anomalyProbMetrics[] = new String[allAnomalies.length];

        // New metrics
        double[] anomalyProbs = new double[allAnomalies.length];
        for (int i = 0; i < allAnomalies.length; i++) {
            String anomaly = allAnomalies[i];
            anomalyProbMetrics[i] = ClusterConstants.INC_PROB_PREFIX + anomaly;
            Double inclusionProbability = clusterCounter.getLabelInclusionProbability(clusterId).get(anomaly);

            if (inclusionProbability == null)
                anomalyProbs[i] = 0;
            else
                anomalyProbs[i] = inclusionProbability;
        }

        return sample.extend(anomalyProbMetrics, anomalyProbs);
    }

    public synchronized void resetCounters() {
        clusterCounter.reset();
    }

    @Override
    public String toString() {
        return "cluster labeling algorithm";
    }
}
