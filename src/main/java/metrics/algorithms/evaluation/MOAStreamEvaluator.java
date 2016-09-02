package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class MOAStreamEvaluator extends AbstractAlgorithm {

    public static final String PRECISION_METRIC = "_overall_precision_";

    private long sampleInterval;
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

    // If extendSample is true, the overall precision will be added to outgoing samples
    public MOAStreamEvaluator(long sampleInterval, boolean printOnRecalculation, boolean extendSample) {
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
    }

    @Override
    public String toString() {
        return "moa stream evaluator";
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        if (sample.hasLabel()) {
            String predictedLabel = sample.getLabel();
            //        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
            String expectedLabel = sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG);
            if (expectedLabel == null) {
                //TODO add handling of unlabled data
                throw new IOException("no expected label found, data not prepared for evaluation (use SourceTrainingLabelingAlgorithm)");
            }
            if (predictedLabel != null && expectedLabel != null) {
                //        if (predictedLabel != null && originalLabel != null) {
                // Cannot evaluate sample without both predicted and original label.
                //TODO: decide wether sampleCount should be incremented for non evaluated samples (affects overall precision an recall)
                //            if (!originalLabel.equals(ClusterConstants.UNKNOWN_LABEL) && sample.getTag(ClusterConstants.BUFFERED_SAMPLE_TAG) == null
                sampleCount++;
                if (!predictedLabel.equals(ClusterConstants.UNKNOWN_LABEL)
                    //                    && !predictedLabel.equals(ClusterConstants.UNCLASSIFIED_CLUSTER)
                        ) {

                    labels.add(predictedLabel);
                    labels.add(expectedLabel);
                    //                    if(predictedLabel == null){
                    //                        System.err.println("null predicted");
                    //                    }
                    //                    if(expectedLabel == null){
                    //                        System.err.println("null expected");
                    //                    }
                    //                    if(expectedLabel.equals("null")){
                    //                        System.err.println("null String");
                    //                    }
                    //                    if(predictedLabel.equals("null")){
                    //                        System.err.println("null String");
                    //                    }
                    //                    if(expectedLabel.isEmpty() || predictedLabel.isEmpty()){
                    //                        System.err.println("String empty");
                    //                    }TODO remove
                    if (predictedLabel.equals(expectedLabel)) {
                        //if labels match, increment counter by 1 for labelToTP
                        correctPredictions++;
                        labelToTP.put(expectedLabel, labelToTP.containsKey(expectedLabel) ? labelToTP.get(expectedLabel) + 1 : 1);

                    } else {
                        wrongPredictions++;
                        //if labels dont match, increment counter by 1 for labelToFP(predictedLabe) and labelTOFN(originalLabel)
                        labelToFP.put(predictedLabel, labelToFP.containsKey(predictedLabel) ? labelToFP.get(predictedLabel) + 1 : 1);
                        labelToFN.put(expectedLabel, labelToFN.containsKey(expectedLabel) ? labelToFN.get(expectedLabel) + 1 : 1);
                    }
                } else {
                    System.err.println("unknown label");
                }
                if (checkRecalculationRequirement()) {
                    recalculate();
                }
            } else {
                System.err.println("WARNING: sample without prediction");
            }
        }

        if (extendSample) {
            return sample.extend(new String[] { PRECISION_METRIC }, new double[] { overallPrecision });
        } else {
            return sample;
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void recalculate() {
        if (labels != null && !labels.isEmpty()) {
            long truePostivesSum = labelToTP.values().stream().mapToInt(Long::intValue).sum();
            long falsePositivesSum = labelToFP.values().stream().mapToInt(Long::intValue).sum();
            long falseNegativesSum = labelToFN.values().stream().mapToInt(Long::intValue).sum();
            overallRecall = (double) truePostivesSum / (double) (truePostivesSum + falsePositivesSum);
            overallPrecision = (double) truePostivesSum / (double) (truePostivesSum + falseNegativesSum);
            labels.forEach(label -> {
                labelToTP.putIfAbsent(label, 0L);
                labelToFP.putIfAbsent(label, 0L);
                labelToFN.putIfAbsent(label, 0L);
                labelToPrecision.put(label, (double) labelToTP.get(label) / (double) (labelToTP.get(label) + labelToFP.get(label)));
                labelToRecall.put(label, (double) labelToTP.get(label) / (double) (labelToTP.get(label) + labelToFN.get(label)));
            });
            averagePrecision = labelToPrecision.values().stream().mapToDouble(d -> d).average().getAsDouble();
            weightedAveragePrecision = labelToPrecision.entrySet().stream().mapToDouble(d -> d.getValue() * (labelToTP.get(d
                    .getKey()) + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / sampleCount;
            averageRecall = labelToRecall.values().stream().mapToDouble(d -> d).average().getAsDouble();
            weightedAverageRecall = labelToRecall.entrySet().stream().mapToDouble(d -> d.getValue() * (labelToTP.get(d.getKey())
                    + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / (double) sampleCount;
            double[] temp = labelToPrecision.values().stream().mapToDouble(d -> d).sorted().toArray();
            medianPrecision = temp[temp.length / 2];
            temp = labelToRecall.values().stream().mapToDouble(d -> d).sorted().toArray();
            medianRecall = temp[temp.length / 2];
            maxPrecision = labelToPrecision.values().stream().mapToDouble(d -> d).max().getAsDouble();
            maxRecall = labelToRecall.values().stream().mapToDouble(d -> d).max().getAsDouble();
            minPrecision = labelToPrecision.values().stream().mapToDouble(d -> d).min().getAsDouble();
            minRecall = labelToRecall.values().stream().mapToDouble(d -> d).min().getAsDouble();
            if (printOnRecalculation) {
                printEvaluation();
            }
        } else {
            if(labels == null) System.err.println("labels null");
            else if(labels.isEmpty()) System.err.println("labels empty");
        }
    }

    private void printEvaluation() {
        System.err.println(getReadableEvaluation());
    }

    private boolean checkRecalculationRequirement() {
        return (sampleCount % sampleInterval) == 0;
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

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        recalculate();
        super.inputClosed(output);
    }
}
