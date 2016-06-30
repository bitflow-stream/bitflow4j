package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import metrics.io.window.MultiHeaderWindow;

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
            if (metricName.startsWith(AdvancedClusterLabelingAlgorithm.INC_PROB_PREFIX)) {
                String normalMetricName = metricName.replace(AdvancedClusterLabelingAlgorithm.INC_PROB_PREFIX, "");

                LabelInclusionProbabilityPredictionWindow stat = window.getWindow(metricName);
                labelInclusionAvgProbabilities.put(normalMetricName, stat.labelInclusionProbabilityAverage());
            }
        }
        //Sort Map by value and recommend best value (except of unknown)
        List<Map.Entry<String, Double>> sortedLabelInclusionAvgProbabilities = labelInclusionAvgProbabilities.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
//        System.out.println(sortedLabelInclusionAvgProbabilities);
        String recommendedLabel = ClusterConstants.UNKNOWN_LABEL;
        for (Map.Entry<String, Double> labelInclusionAvgProbability : sortedLabelInclusionAvgProbabilities) {
            if (!labelInclusionAvgProbability.getKey().equals(ClusterConstants.UNKNOWN_LABEL)) {
                recommendedLabel = labelInclusionAvgProbability.getKey();
                break;
            }
        }

        int labelClusterId;
        String originalLabel;

        try {
            labelClusterId = Integer.parseInt(sample.getTag(ClusterConstants.CLUSTER_TAG));
            originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException(
                    "Sample not prepared for labeling, add a clusterer to the pipeline or fix current clusterer (failed to extract cluster id from point label or original label not found).");
        }

        Sample sampleToReturn = new Sample(sample.getHeader(), sample.getMetrics(), sample.getTimestamp(), sample.getSource(),
                recommendedLabel);
        sampleToReturn.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        sampleToReturn.setTag(ClusterConstants.CLUSTER_TAG, String.valueOf(labelClusterId));
        
//        System.out.println("###########------    org: "+ originalLabel+ "   rec: "+recommendedLabel+"      ---------------##########");
        return sampleToReturn;
    }

    @Override
    public String toString() {
        return "label aggregator algorithm";
    }

}
