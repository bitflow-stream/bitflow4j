package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ExtendedStreamEvaluator extends StreamEvaluator {

    private Set<String> labels = new HashSet<>();
    private Map<String, Long> truePositives = new HashMap<>();
    private Map<String, Long> falseNegatives = new HashMap<>();
    private Map<String, Long> falsePositives = new HashMap<>();
    private Map<String, Double> labelToPrecision = new HashMap<>();
    private Map<String, Double> labelToRecall = new HashMap<>();
    private long sampleCount, unclassifiedSamples;
    private double averagePrecision, averageRecall, overallRecall, weightedAverageRecall, weightedAveragePrecision, minPrecision, minRecall, maxPrecision, maxRecall;
    private double medianRecall, medianPrecision;

    public ExtendedStreamEvaluator(boolean extendSample) {
        super(extendSample);
    }

    protected void correctSample(Sample sample) {
        super.correctSample(sample);
        increment(truePositives, sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG));
    }

    protected void incorrectSample(Sample sample) {
        super.incorrectSample(sample);
        increment(falsePositives, sample.getLabel());
        increment(falseNegatives, sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG));
    }

    private void increment(Map<String, Long> map, String key) {
        map.put(key, map.containsKey(key) ? map.get(key) + 1 : 1);
    }

    protected Boolean isCorrectPrediction(Sample sample) {
        // TODO: decide wether sampleCount should be incremented for non evaluated samples
        // (affects overall precision an recall)
        if (!sample.hasLabel() || !sample.hasTag(ClusterConstants.EXPECTED_PREDICTION_TAG)) {
            return null;
        }

        String predictedLabel = sample.getLabel();
        String expectedLabel = sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG);
        sampleCount++;
        labels.add(predictedLabel);
        labels.add(expectedLabel);
        return predictedLabel.equals(expectedLabel);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected void recalculate() {
        if (labels != null && !labels.isEmpty()) {
            long truePostivesSum = truePositives.values().stream().mapToInt(Long::intValue).sum();
            long falsePositivesSum = falsePositives.values().stream().mapToInt(Long::intValue).sum();
            long falseNegativesSum = falseNegatives.values().stream().mapToInt(Long::intValue).sum();
            overallRecall = (double) truePostivesSum / (double) (truePostivesSum + falsePositivesSum);
            overallPrecision = (double) truePostivesSum / (double) (truePostivesSum + falseNegativesSum);
            labels.forEach(label -> {
                truePositives.putIfAbsent(label, 0L);
                falsePositives.putIfAbsent(label, 0L);
                falseNegatives.putIfAbsent(label, 0L);
                labelToPrecision.put(label, (double) truePositives.get(label) / (double) (truePositives.get(label) + falsePositives.get(label)));
                labelToRecall.put(label, (double) truePositives.get(label) / (double) (truePositives.get(label) + falseNegatives.get(label)));
            });
            averagePrecision = labelToPrecision.values().stream().mapToDouble(d -> d).average().getAsDouble();
            weightedAveragePrecision = labelToPrecision.entrySet().stream().mapToDouble(d -> d.getValue() * (truePositives.get(d
                    .getKey()) + falsePositives.get(d.getKey()))).average().getAsDouble() * labels.size() / sampleCount;
            averageRecall = labelToRecall.values().stream().mapToDouble(d -> d).average().getAsDouble();
            weightedAverageRecall = labelToRecall.entrySet().stream().mapToDouble(d -> d.getValue() * (truePositives.get(d.getKey())
                    + falsePositives.get(d.getKey()))).average().getAsDouble() * labels.size() / (double) sampleCount;
            double[] temp = labelToPrecision.values().stream().mapToDouble(d -> d).sorted().toArray();
            medianPrecision = temp[temp.length / 2];
            temp = labelToRecall.values().stream().mapToDouble(d -> d).sorted().toArray();
            medianRecall = temp[temp.length / 2];
            maxPrecision = labelToPrecision.values().stream().mapToDouble(d -> d).max().getAsDouble();
            maxRecall = labelToRecall.values().stream().mapToDouble(d -> d).max().getAsDouble();
            minPrecision = labelToPrecision.values().stream().mapToDouble(d -> d).min().getAsDouble();
            minRecall = labelToRecall.values().stream().mapToDouble(d -> d).min().getAsDouble();
        } else {
            if(labels == null) System.err.println("labels null");
            else if(labels.isEmpty()) System.err.println("labels empty");
        }
    }

    private void printEvaluation() {
        System.out.println(getReadableEvaluation());
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        recalculate();
        printEvaluation();
        super.inputClosed(output);
    }

    protected boolean shouldRecalculate() {
        // Only recalculate when finished collecting data (in inputClosed())
        return false;
    }

    private final static String LINE = "----------------------------------------------";
    private final static String HASH = "##############################################";
    private final static String NEW_LINE = System.getProperty("line.separator");

    public String getReadableEvaluation() {
        StringBuilder builder = new StringBuilder();
        builder.append(HASH);
        builder.append(NEW_LINE);
        builder.append("CLUSTER EVALUATION");
        builder.append(NEW_LINE);
        builder.append("Number of samples: ");
        builder.append(sampleCount);
        builder.append(NEW_LINE);
        builder.append("Number of correct predictions: ");
        builder.append(correctPredictions);
        builder.append(NEW_LINE);
        builder.append("Number of wrong predictions: ");
        builder.append(wrongPredictions);
        builder.append(NEW_LINE);
        builder.append("Ratio of correct to wrong predictions: ");
        builder.append((double) correctPredictions / wrongPredictions);
        builder.append(NEW_LINE);
        builder.append("Overall precision: ");
        builder.append(overallPrecision);
        builder.append(NEW_LINE);
        builder.append("Overal recall: ");
        builder.append(overallRecall);
        builder.append(NEW_LINE);
        builder.append("Average precision: ");
        builder.append(averagePrecision);
        builder.append(NEW_LINE);
        builder.append("Average Recall: ");
        builder.append(averageRecall);
        builder.append(NEW_LINE);
        builder.append("Weighted average precision: ");
        builder.append(weightedAveragePrecision);
        builder.append(NEW_LINE);
        builder.append("Weighted average recall: ");
        builder.append(weightedAverageRecall);
        builder.append(NEW_LINE);
        builder.append("Min precision: ");
        builder.append(minPrecision);
        builder.append(NEW_LINE);
        builder.append("Max precision: ");
        builder.append(maxPrecision);
        builder.append(NEW_LINE);
        builder.append("Min recall: ");
        builder.append(minRecall);
        builder.append(NEW_LINE);
        builder.append("Max recall: ");
        builder.append(maxRecall);
        builder.append(NEW_LINE);
        builder.append("Precision median: ");
        builder.append(medianPrecision);
        builder.append(NEW_LINE);
        builder.append("Recall median: ");
        builder.append(medianRecall);
        builder.append(NEW_LINE);
        builder.append(LINE);
        builder.append(NEW_LINE);
        builder.append("Label Values");
        labelsToString(builder);
        return builder.toString();
    }

    private void labelsToString(StringBuilder builder) {
        labels.forEach(label -> {
            builder.append(NEW_LINE);
            builder.append(LINE);
            builder.append(NEW_LINE);
            builder.append("Label: ");
            builder.append(label);
            builder.append(NEW_LINE);
            builder.append("Precision: ");
            builder.append(getPrecision(label));
            builder.append(NEW_LINE);
            builder.append("Recall: ");
            builder.append(getRecall((label)));
            builder.append(NEW_LINE);
            builder.append("true positives: ");
            builder.append(getTruePositives(label));
            builder.append(NEW_LINE);
            builder.append("false positives: ");
            builder.append(getFalsePositives(label));
            builder.append(NEW_LINE);
//            builder.append("true negatives: ");
//            builder.append(labelTrueNegatives.get(label));
//            builder.append(NEW_LINE);
            builder.append("false negatives: ");
            builder.append(getFalseNegatives(label));
            builder.append(NEW_LINE);
        });
    }

    private double getFalseNegatives(String label) {
        return falseNegatives.get(label);
    }

    private long getFalsePositives(String label) {
        return falsePositives.get(label);
    }

    private long getTruePositives(String label) {
        return truePositives.get(label);
    }

    private double getRecall(String label) {
        return labelToRecall.get(label);
    }

    private double getPrecision(String label) {
        return labelToPrecision.get(label);
    }

}
