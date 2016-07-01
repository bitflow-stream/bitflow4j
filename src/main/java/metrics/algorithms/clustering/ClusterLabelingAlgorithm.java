package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;

import static metrics.algorithms.clustering.ClusterConstants.INC_PROB_PREFIX;

/**
 * This labeling algorithm extends the metrics with the inclusion probability to given labels. 
 * Created by fschmidt on 29.06.2016.
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    private final ClusterCounter clusterCounter;
    private final boolean includeProbabilities;

    public ClusterLabelingAlgorithm(double thresholdToClassify, boolean includeProbabilities) {
        this.clusterCounter = new ClusterCounter(thresholdToClassify);
        this.includeProbabilities = includeProbabilities;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        int labelClusterId = sample.getClusterId();
        String originalLabel = sample.getLabel();
        clusterCounter.increment(labelClusterId, originalLabel);
        String newLabel = clusterCounter.calculateLabel(labelClusterId);

        Sample sampleToReturn;
        if (includeProbabilities) {
            // Extend Header
            String allAnomalies[] = clusterCounter.getLabelInclusionProbability(labelClusterId).keySet().toArray(new String[0]);
            for (int i = 0; i < allAnomalies.length; i++) {
                allAnomalies[i] = INC_PROB_PREFIX + allAnomalies[i];
            }

            // Extend Metrics
            double[] anomalyProbs = new double[allAnomalies.length];
            for (int i = 0; i < allAnomalies.length; i++) {
                String anomaly = allAnomalies[i];
                Double inclusionProbability = clusterCounter.getLabelInclusionProbability(labelClusterId).get(anomaly);
                if (inclusionProbability == null)
                    anomalyProbs[i] = 0;
                else
                    anomalyProbs[i] = inclusionProbability;
            }
            sampleToReturn = sample.extend(allAnomalies, anomalyProbs);
        } else {
            sampleToReturn = new Sample(sample);
        }

        sampleToReturn.setLabel(newLabel);
        sampleToReturn.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        return sampleToReturn;
    }

    @Override
    public String toString() {
        return "cluster labeling algorithm";
    }
}
