package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Set;

import static metrics.algorithms.clustering.ClusterConstants.INC_PROB_PREFIX;

/**
 * This labeling algorithm extends the metrics with the inclusion probability to given labels. 
 * Created by fschmidt on 29.06.2016.
 * @author mbyfield, fschmidt
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    private final ClusterCounter clusterCounter;
    private final boolean includeProbabilities;
    private final boolean stripData;
    private final Set<String> trainedLabels;

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
        if (originalLabel != null && labelClusterId >= 0 && (trainedLabels == null || trainedLabels.contains(originalLabel)) && sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null)
            clusterCounter.increment(labelClusterId, originalLabel);
        String newLabel = sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null? clusterCounter.calculateLabel(labelClusterId) : ClusterConstants.BUFFERED_LABEL;

        Sample sampleToReturn;
        if (includeProbabilities && sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null) {
            // New header
            String allAnomalies[] = clusterCounter.getAllLabels().toArray(new String[0]);

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

            for (int i = 0; i < allAnomalies.length; i++) {
                allAnomalies[i] = INC_PROB_PREFIX + allAnomalies[i];
            }
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
