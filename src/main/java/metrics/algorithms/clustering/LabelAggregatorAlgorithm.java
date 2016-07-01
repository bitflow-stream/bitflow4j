package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.window.MultiHeaderWindow;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static metrics.algorithms.clustering.ClusterConstants.INC_PROB_PREFIX;

/**
 * Created by fschmidt on 29.06.2016.
 */
public class LabelAggregatorAlgorithm extends AbstractAlgorithm {

    // TODO allow multiple window sizes
    private final MultiHeaderWindow<LabelInclusionProbabilityPredictionWindow> window;

    public LabelAggregatorAlgorithm(int windowSize) {
        window = new MultiHeaderWindow<>(windowSize, LabelInclusionProbabilityPredictionWindow.FACTORY);
    }

    public LabelAggregatorAlgorithm(long windowTimespan) {
        window = new MultiHeaderWindow<>(windowTimespan, LabelInclusionProbabilityPredictionWindow.FACTORY);
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        window.add(sample);

        Map<String, Double> labelInclusionAvgProbabilities = new HashMap<>();
        for (String metricName : window.allMetricNames()) {
            if (metricName.startsWith(INC_PROB_PREFIX)) {
                String anomalyName = metricName.replace(INC_PROB_PREFIX, "");

                LabelInclusionProbabilityPredictionWindow stat = window.getWindow(metricName);
                labelInclusionAvgProbabilities.put(anomalyName, stat.labelInclusionProbabilityAverage());
            }
        }
        // Sort Map by value and recommend best value (except of unknown)
        List<Map.Entry<String, Double>> sortedLabelInclusionAvgProbabilities = labelInclusionAvgProbabilities.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
        String recommendedLabel = ClusterConstants.UNKNOWN_LABEL;
        for (Map.Entry<String, Double> labelInclusionAvgProbability : sortedLabelInclusionAvgProbabilities) {
            if (!labelInclusionAvgProbability.getKey().equals(ClusterConstants.UNKNOWN_LABEL)) {
                recommendedLabel = labelInclusionAvgProbability.getKey();
                break;
            }
        }

        Sample sampleToReturn = new Sample(sample);
        // Possibly overwrites the label previously predicted by ClusterLabelingAlgorithm
        sampleToReturn.setLabel(recommendedLabel);
        return sampleToReturn;
    }

    @Override
    public String toString() {
        return "label aggregator algorithm";
    }

}
