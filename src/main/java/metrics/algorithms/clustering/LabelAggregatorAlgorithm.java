package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.MetricOutputStream;
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
    public static final int DEFAULT_WINDOW_SIZE = 10;
//    public static final long DEFAULT_WINDOW_TIMESPAN = 1000;
    protected boolean stripData = false;

    private final MultiHeaderWindow<LabelInclusionProbabilityPredictionWindow> window;
    private int bufferedSamples;

    public LabelAggregatorAlgorithm(){
        window = new MultiHeaderWindow<LabelInclusionProbabilityPredictionWindow>(DEFAULT_WINDOW_SIZE, LabelInclusionProbabilityPredictionWindow.FACTORY);
    }


    public LabelAggregatorAlgorithm(int windowSize) {
        window = new MultiHeaderWindow<>(windowSize, LabelInclusionProbabilityPredictionWindow.FACTORY);
    }

    public LabelAggregatorAlgorithm(long windowTimespan) {
        window = new MultiHeaderWindow<>(windowTimespan, LabelInclusionProbabilityPredictionWindow.FACTORY);
    }

    public LabelAggregatorAlgorithm stripData(){
        this.stripData = true;
        return this;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        Sample sampleToReturn;
        if (sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null) {
            window.add(sample);

            Map<String, Double> labelInclusionAvgProbabilities = new HashMap<>();
            for (String metricName : window.allMetricNames()) {
                if (metricName.startsWith(INC_PROB_PREFIX)) {
                    String anomalyName = metricName.replace(INC_PROB_PREFIX, "");
                    //TODO clarify what happens here
                    LabelInclusionProbabilityPredictionWindow stat = window.getWindow(metricName);
                    labelInclusionAvgProbabilities.put(anomalyName, stat.labelInclusionProbabilityAverage());
                }
            }
            if(labelInclusionAvgProbabilities.isEmpty()) System.out.println("AÖLKHFHBFRABFSIPASDHGKJSDHVNLJCXNISBHCAHSDBGAJKLHDAKSJDB");
//            for (String temp : labelInclusionAvgProbabilities.keySet()){
//                if(temp == null || temp.isEmpty() || temp.equalsIgnoreCase("null")){
//                    System.out.println("b");
//                };
//            }
            // Sort Map by value and recommend best value (except of unknown)
            List<Map.Entry<String, Double>> sortedLabelInclusionAvgProbabilities = labelInclusionAvgProbabilities.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
//            for (Map.Entry<String, Double> temp : sortedLabelInclusionAvgProbabilities){
//                if(temp.getKey() == null || temp.getKey().isEmpty() || temp.getKey().equalsIgnoreCase("null")){
//                    System.out.println("b");
//                };
//            }
            String recommendedLabel = ClusterConstants.UNKNOWN_LABEL;

            for (Map.Entry<String, Double> labelInclusionAvgProbability : sortedLabelInclusionAvgProbabilities) {
                if (!labelInclusionAvgProbability.getKey().equals(ClusterConstants.UNKNOWN_LABEL)) {
                    recommendedLabel = labelInclusionAvgProbability.getKey();
//                    if(recommendedLabel == null || recommendedLabel.isEmpty() || recommendedLabel.equalsIgnoreCase("null")){
//                        System.out.println("b");
//                        recommendedLabel = ClusterConstants.NO_LABEL_FOR_WINDOW;
//                    }TODO remove
                    break;
                } else {
                    System.out.println("ERROR: UNKNOWN LABEL IN LABEL AGGREGATOR ALGORITHM");
                }
            }
            if (recommendedLabel.equals(ClusterConstants.UNKNOWN_LABEL)) System.out.println("ERRRROR");
            sampleToReturn = new Sample(sample);
            // Possibly overwrites the label previously predicted by ClusterLabelingAlgorithm
            sampleToReturn.setLabel(recommendedLabel);
//            return sampleToReturn;
        } else {
            bufferedSamples++;
            sampleToReturn = sample;
        }
        if(stripData) sampleToReturn = sampleToReturn.removeMetricsWithPrefix(INC_PROB_PREFIX);
        return sampleToReturn;
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        super.inputClosed(output);
        System.out.println("number of buffered samples: " + bufferedSamples);
    }

    @Override
    public String toString() {
        return "label aggregator algorithm";
    }

}
