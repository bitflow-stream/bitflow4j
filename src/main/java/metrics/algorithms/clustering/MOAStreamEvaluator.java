package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class MOAStreamEvaluator extends AbstractAlgorithm {

    private final static String LINE = "----------------------------------------------";
    private final static String HASH = "##############################################";
    private final static String NEW_LINE = System.getProperty("line.separator");
    private static final String INCORRECT_HEADERS = "Incorrect headers found. Try adding a MOAStreamClusterer to the Algorithm pipeline.";
    //    private ExternalClusterer clusterer;
    private boolean removeEvaluationTag;
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

    public MOAStreamEvaluator(boolean removeEvaluationTag, long sampleInterval, boolean printOnRecalculation) {
        this.removeEvaluationTag = removeEvaluationTag;
        this.sampleInterval = sampleInterval;
        this.printOnRecalculation = printOnRecalculation;
//        this.clusterer = clusterer;
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

        sampleCount++;
//        String[] parsedLabels = sample.getLabel().split(MOAStreamClusterer.LABEL_SEPARATOR);
//        if (parsedLabels == null || parsedLabels.length != 3) throw new IOException("Incompatible Label found for sample; missing separators for predicted label and cluster, consider adding a MOAStreamClusterer to the Algorithm pipeline and set ");
//        String originalLabel = parsedLabels[0];
        //get Original Label and clusterid from tags if available
        String predictedLabel = sample.getLabel();
        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        labels.add(predictedLabel);
        labels.add(originalLabel);
//        try {
//            int clusterId = Integer.parseInt(sample.getTag(MOAStreamClusterer.CLUSTER_TAG));
//        } catch (NumberFormatException e){
//            throw new IOException(INCORRECT_HEADERS);
//        }
        if (predictedLabel == null || originalLabel == null)
            throw new IOException(INCORRECT_HEADERS);
        if (predictedLabel.equals(originalLabel)) {
            //if labels match, increment counter by 1 for labelToTP
            correctPredictions++;
            labelToTP.put(originalLabel, labelToTP.containsKey(originalLabel) ? labelToTP.get(originalLabel) + 1 : 1);
        } else {
            wrongPredictions++;
            //if labels dont match, increment counter by 1 for labelToFP(predictedLabe) and labelTOFN(originalLabel)
            if (!(predictedLabel.equals(ClusterConstants.NOISE_CLUSTER) || predictedLabel.equals(ClusterConstants.UNCLASSIFIED_CLUSTER))) {
            }
            labelToFP.put(predictedLabel, labelToFP.containsKey(predictedLabel) ? labelToFP.get(predictedLabel) + 1 : 1);
            labelToFN.put(originalLabel, labelToFN.containsKey(originalLabel) ? labelToFN.get(originalLabel) + 1 : 1);
        }
        if (checkRecalculationRequirement()) recalculate();

        Sample sampleToReturn = new Sample(sample.getHeader(), sample.getMetrics(), sample);
        if (removeEvaluationTag) {
            sampleToReturn.deleteTag(ClusterConstants.ORIGINAL_LABEL_TAG);
            sampleToReturn.deleteTag(ClusterConstants.CLUSTER_TAG);
        }
        return sampleToReturn;
    }

    private void recalculate() {
        truePostivesSum = labelToTP.values().stream().mapToInt(i -> i.intValue()).sum();
        falsePositivesSum = labelToFP.values().stream().mapToInt(i -> i.intValue()).sum();
        falseNegativesSum = labelToFN.values().stream().mapToInt(i -> i.intValue()).sum();
        overallRecall = (double) truePostivesSum / (truePostivesSum + falsePositivesSum);
        overallPrecision = (double) truePostivesSum / (truePostivesSum + falseNegativesSum);
        labels.forEach(label -> {
            if (labelToTP.get(label) == null) labelToTP.put(label, 0L);
            if (labelToFP.get(label) == null) labelToFP.put(label, 0L);
            if (labelToFN.get(label) == null) labelToFN.put(label, 0L);
            labelToPrecision.put(label, (double) labelToTP.get(label) / (labelToTP.get(label) + labelToFP.get(label)));
            labelToRecall.put(label, (double) labelToTP.get(label) / (labelToTP.get(label) + labelToFN.get(label)));
        });
        averagePrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble();
        weightedAveragePrecision = labelToPrecision.entrySet().stream().mapToDouble(d -> d.getValue().doubleValue() * (labelToTP.get(d.getKey()) + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / sampleCount;
        averageRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble();
        weightedAverageRecall = labelToRecall.entrySet().stream().mapToDouble(d -> d.getValue().doubleValue() * (labelToTP.get(d.getKey()) + labelToFP.get(d.getKey()))).average().getAsDouble() * labels.size() / sampleCount;
        double[] temp = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).sorted().toArray();
        medianPrecision = temp[temp.length / 2];
        temp = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).sorted().toArray();
        medianRecall = temp[temp.length / 2];
        maxPrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).max().getAsDouble();
        maxRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).max().getAsDouble();
        minPrecision = labelToPrecision.values().stream().mapToDouble(d -> d.doubleValue()).min().getAsDouble();
        minRecall = labelToRecall.values().stream().mapToDouble(d -> d.doubleValue()).min().getAsDouble();
        if (printOnRecalculation) printEvaluation();
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
