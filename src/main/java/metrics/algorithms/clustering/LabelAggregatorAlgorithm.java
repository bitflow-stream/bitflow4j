package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.window.MetricStatisticsWindow;
import metrics.io.window.MultiHeaderWindow;

import java.io.IOException;

import static metrics.algorithms.clustering.ClusterConstants.INC_PROB_PREFIX;

/**
 * This can be added after ClusterLabelingAlgorithm to smooth out sudden changes in the result data.
 * Instead of always outputting the best label for the most recent cluster, there is a short window that stores
 * a running average of the probabilities for all labels that pass through here. If many different labels are used,
 * this smoothes the results a bit.
 *
 * Created by fschmidt on 29.06.2016.
 */
public class LabelAggregatorAlgorithm extends AbstractAlgorithm {

    // Avoid recommending labels with a zero (or close-to-zero) probability
    private static final double MIN_VALID_PROBABILITY = 0.01;

    protected boolean stripData = false;
    private final MultiHeaderWindow<MetricStatisticsWindow> window;

    public LabelAggregatorAlgorithm(int windowSize) {
        window = new MultiHeaderWindow<>(windowSize, MetricStatisticsWindow.FACTORY);
    }

    public LabelAggregatorAlgorithm(long windowTimespan) {
        window = new MultiHeaderWindow<>(windowTimespan, MetricStatisticsWindow.FACTORY);
    }

    public LabelAggregatorAlgorithm stripData(){
        this.stripData = true;
        return this;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        window.add(sample);

        // TODO this relies on the includeProbabilities flag of ClusterLabelingAlgorithm
        // There should be a check that this flag is enabled and ClusterLabelingAlgorithm is part of the pipeline...

        String recommendedLabel = ClusterConstants.UNCLASSIFIED_CLUSTER;
        double maxProbability = -Double.MAX_VALUE;
        for (String metricName : window.allMetricNames()) {
            if (metricName.startsWith(INC_PROB_PREFIX)) {
                double avgProbability = window.getWindow(metricName).average();
                if (avgProbability > MIN_VALID_PROBABILITY && avgProbability > maxProbability) {
                    maxProbability = avgProbability;
                    recommendedLabel = metricName.replace(INC_PROB_PREFIX, "");
                }
            }
        }
        if (stripData)
            sample = sample.removeMetricsWithPrefix(INC_PROB_PREFIX);

        // Possibly overwrites the label previously predicted by ClusterLabelingAlgorithm
        sample.setLabel(recommendedLabel);
        return sample;
    }

    @Override
    public String toString() {
        return "label aggregator algorithm";
    }

}
