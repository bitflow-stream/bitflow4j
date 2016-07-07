package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Malcolm-X, fschmidt on 07.07.2016.
 */
public class MOAStreamAnomalyDetectionEvaluator extends AbstractAlgorithm {

    public static final String PRECISION_METRIC = "_overall_precision_";

    private final static String LINE = "----------------------------------------------";
    private final static String HASH = "##############################################";
    private final static String NEW_LINE = System.getProperty("line.separator");
    private static final String INCORRECT_HEADERS = "Incorrect headers found. Try adding a MOAStreamClusterer to the Algorithm pipeline.";

    //    private ExternalClusterer clusterer;
    private long sampleInterval;
    private long truePostivesSum, falsePositivesSum, falseNegativesSum;
    private Set<String> labels;
    /**
     * Maps for tp, fp, fn
     */
    private HashMap<String, Long> labelToTP, labelToFN, labelToFP;
    private HashMap<String, Double> labelToPrecision, labelToRecall;
    private long sampleCount, unclassifiedSamples;
    private double overallPrecision, averagePrecision, averageRecall, overallRecall, weightedAverageRecall, weightedAveragePrecision, minPrecision, minRecall, maxPrecision, maxRecall;
    private long correctPredictions;
    private long wrongPredictions;
    private double medianRecall;
    private double medianPrecision;
    private boolean printOnRecalculation;
    private final boolean extendSample;

    private final Set<String> trainedLabels;

    // If extendSample is true, the overall precision will be added to outgoing samples
    public MOAStreamAnomalyDetectionEvaluator(long sampleInterval, boolean printOnRecalculation, boolean extendSample,
            Set<String> trainedLabels) {
        this.extendSample = extendSample;
        this.sampleInterval = sampleInterval;
        this.printOnRecalculation = printOnRecalculation;
        sampleCount = 0;
        labelToFN = new HashMap<>();
        labelToFP = new HashMap<>();
        labelToTP = new HashMap<>();
        labelToPrecision = new HashMap<>();
        labelToRecall = new HashMap<>();
        labels = new HashSet<>();
        this.trainedLabels = trainedLabels;
    }

    @Override
    public String toString() {
        return "moa stream evaluator";
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        String predictedLabel = sample.getLabel();
        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);

        if (predictedLabel != null && originalLabel != null) {
            // Cannot evaluate sample without both predicted and original label.
            sampleCount++;
            labels.add(predictedLabel);
            labels.add(originalLabel);

            if ((trainedLabels.contains(originalLabel) && trainedLabels.contains(predictedLabel)) || (!trainedLabels
                    .contains(predictedLabel) && !trainedLabels.contains(originalLabel))) {
                correctPredictions++;
                labelToTP.put(originalLabel, labelToTP.containsKey(originalLabel) ? labelToTP.get(originalLabel) + 1 : 1);

            } else {
                wrongPredictions++;
                //if labels dont match, increment counter by 1 for labelToFP(predictedLabe) and labelTOFN(originalLabel)
                labelToFP.put(predictedLabel, labelToFP.containsKey(predictedLabel) ? labelToFP.get(predictedLabel) + 1 : 1);
                labelToFN.put(originalLabel, labelToFN.containsKey(originalLabel) ? labelToFN.get(originalLabel) + 1 : 1);
            }

            if (checkRecalculationRequirement()) {
                recalculate();
            }
        }

        if (extendSample) {
            return sample.extend(new String[]{PRECISION_METRIC}, new double[]{overallPrecision});
        } else {
            return sample;
        }
    }

    private void recalculate() {
        truePostivesSum = labelToTP.values().stream().mapToInt(i -> i.intValue()).sum();
        falsePositivesSum = labelToFP.values().stream().mapToInt(i -> i.intValue()).sum();
        falseNegativesSum = labelToFN.values().stream().mapToInt(i -> i.intValue()).sum();
        overallRecall = (double) truePostivesSum / (double) (truePostivesSum + falsePositivesSum);
        overallPrecision = (double) truePostivesSum / (double) (truePostivesSum + falseNegativesSum);
        labels.forEach(label -> {
            labelToTP.putIfAbsent(label, 0L);
            labelToFP.putIfAbsent(label, 0L);
            labelToFN.putIfAbsent(label, 0L);
            labelToPrecision.put(label, (double) labelToTP.get(label) / (double) (labelToTP.get(label) + labelToFP.get(label)));
            labelToRecall.put(label, (double) labelToTP.get(label) / (double) (labelToTP.get(label) + labelToFN.get(label)));
        });
        averagePrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble();
        weightedAveragePrecision = labelToPrecision.entrySet().stream().mapToDouble(d -> d.getValue().doubleValue() * (labelToTP.get(d
                .getKey()) + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / sampleCount;
        averageRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble();
        weightedAverageRecall = labelToRecall.entrySet().stream().mapToDouble(d -> d.getValue().doubleValue() * (labelToTP.get(d.getKey())
                + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / (double) sampleCount;
        double[] temp = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).sorted().toArray();
        medianPrecision = temp[temp.length / 2];
        temp = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).sorted().toArray();
        medianRecall = temp[temp.length / 2];
        maxPrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).max().getAsDouble();
        maxRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).max().getAsDouble();
        minPrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).min().getAsDouble();
        minRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).min().getAsDouble();
        if (printOnRecalculation) {
            printEvaluation();
        }
    }

    private void printEvaluation() {
        System.out.println(getReadableEvaluation());
    }

    private boolean checkRecalculationRequirement() {
        return (sampleCount % sampleInterval) == 0;
    }

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
        return labelToFN.get(label);
    }

    private long getFalsePositives(String label) {
        return labelToFP.get(label);
    }

    private long getTruePositives(String label) {
        return labelToTP.get(label);
    }

    private double getRecall(String label) {
        return labelToRecall.get(label);
    }

    private double getPrecision(String label) {
        return labelToPrecision.get(label);
    }
//    private void ParseLabel(String labelFromSample) throws IllegalArgumentException {
//        String[]
//        originalLabel =
//    }
}
