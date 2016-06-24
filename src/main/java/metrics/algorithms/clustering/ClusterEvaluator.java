package metrics.algorithms.clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author fschmidt
 */
public class ClusterEvaluator {
    
    private final String UNCLASSIFIED_CLUSTER = "unclassified";
    private final String NOISE_CLUSTER = "noise";
    private final String FAKE_LABEL = "FAKE";
    private final Map<Integer, MOAStreamClusterer.ClusterCounters> clusterLabelMaps;
    private final double thresholdToClassifyCluster;
    private Map<String, Integer> labelFalsePositives, labelTruePositives, labelFalseNegatives, labelTrueNegatives;
    private Map<Integer, String> classifiedClusters;
        private long truePositives;
    private long trueNegatives, falseNegatives, falsePositives;
    private int numberOfPoints, unclassifiedPoints, unclassifiedClusters, noisePoints;

    public ClusterEvaluator(Map<Integer, MOAStreamClusterer.ClusterCounters> clusterLabelMaps, double thresholdToClassifyCluster, int numberOfPoints) {
        this.clusterLabelMaps = clusterLabelMaps;
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
        this.numberOfPoints = numberOfPoints;
        initEmptyFields();
        this.evaluate();
    }

    private void evaluate() {
        calculateAllBestLabels();
        calculateLabelQuality();
        calculateOverallQuality();
    }

    private void calculateOverallQuality() {
    }

    private void calculateLabelQuality() {
        initLabelMaps();
        classifiedClusters.entrySet().forEach((labeledClusterId) -> {
        int clusterId = labeledClusterId.getKey();
        String clusterLabel = labeledClusterId.getValue();

            clusterLabelMaps.get(clusterId).counters.entrySet().forEach(countersEntry -> {
//            totalOverallCount += pointCount;
            String pointLabel = countersEntry.getKey();
            int pointCount = countersEntry.getValue();
            if(pointLabel.equals(clusterLabel) ){
                labelTruePositives.replace(clusterLabel, labelTruePositives
                        .get(clusterLabel) + pointCount);
                //performance issues
//                classifiedClusters.values().stream().distinct().filter(label -> !label.equals(pointLabel)).forEach(label -> {
//                    labelTrueNegatives.put(label, labelTrueNegatives.containsKey(label) ? labelTrueNegatives.get(label) + pointCount: pointCount);
//                });

            }else{
                if ((!pointLabel.equals(NOISE_CLUSTER)) && !pointLabel.equals(UNCLASSIFIED_CLUSTER)) {
                    labelFalsePositives.replace(clusterLabel, labelFalsePositives.containsKey(clusterLabel) ? labelFalsePositives
                            .get(clusterLabel) + pointCount : pointCount);
                }
                labelFalseNegatives.replace(pointLabel, labelFalseNegatives.containsKey(clusterLabel) ? labelFalseNegatives
                        .get(clusterLabel) + pointCount : pointCount);
            }

        });
        });
        labelTruePositives.entrySet().forEach(entry -> labelTrueNegatives.replace(entry.getKey() , numberOfPoints - (entry.getValue() + labelFalseNegatives.get(entry.getKey()) + labelFalsePositives.get(entry.getKey()))));
        truePositives =  labelTruePositives.values().stream().mapToInt(i -> i.intValue()).sum();;
        trueNegatives =  labelTrueNegatives.values().stream().mapToInt(i -> i.intValue()).sum();;
        falsePositives =  labelFalsePositives.values().stream().mapToInt(i -> i.intValue()).sum();;
        falseNegatives =  labelFalseNegatives.values().stream().mapToInt(i -> i.intValue()).sum();;
    }

    private void initLabelMaps() {
        classifiedClusters.values().stream().distinct().forEach(label -> {
            labelFalseNegatives.put(label,0);
            labelFalsePositives.put(label,0);
            labelTruePositives.put(label,0);
            labelTrueNegatives.put(label,0);
        });
    }


    private void initEmptyFields() {
        labelFalseNegatives = new HashMap<>();
        labelFalsePositives = new HashMap<>();
        labelTrueNegatives = new HashMap<>();
        labelTruePositives = new HashMap<>();
        classifiedClusters = new HashMap<>();
//        totalOverallCount = 0;
    }

    private void calculateAllBestLabels() {
        clusterLabelMaps.keySet().stream().forEach((clusterId) -> {

            if (clusterId != -1) {
                MOAStreamClusterer.ClusterCounters clusterCounts = clusterLabelMaps.get(clusterId);
                String bestLabel = clusterCounts.counters.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                        .getValue() ? 1 : -1).get().getKey();
                double bestChance = (double) clusterCounts.counters.get(bestLabel) / (double) clusterCounts.total;
                if (bestChance >= thresholdToClassifyCluster) {
                    classifiedClusters.put(clusterId, bestLabel);
                } else{
                    classifiedClusters.put(clusterId, UNCLASSIFIED_CLUSTER);
                }
            } else {
                classifiedClusters.put(clusterId, NOISE_CLUSTER);
            }
        });
    }

    public Map<Integer, MOAStreamClusterer.ClusterCounters> getClusterLabelMaps() {
        return clusterLabelMaps;
    }

    public double getThresholdToClassifyCluster() {
        return thresholdToClassifyCluster;
    }

    public double getOverallPrecision() {
        return (double) truePositives / (truePositives + falsePositives);
    }

    public double getOverallRecall(){
        return (double) truePositives / (truePositives + falseNegatives);
    }

    public double getRecall(String label){
        return (double) labelTruePositives.get(label) /  (labelTruePositives.get(label) + labelFalseNegatives.get(label));
    }

    public double getPrecision(String label){
        return (double) labelTruePositives.get(label) / (labelTruePositives.get(label) + labelFalsePositives.get(label));
    }

    public Set<String> getLabels(){
        return labelTruePositives.keySet();
    }

    @Override
    public String toString(){
        String line = "----------------------------------------------";
        String hash = "##############################################";
        String newLine = System.getProperty("line.separator");
        StringBuilder builder = new StringBuilder();
        builder.append(hash);
        builder.append(newLine);
        builder.append("CLUSTER EVALUATION");
        builder.append(newLine);
        builder.append("Number of Points: ");
        builder.append(numberOfPoints);
        builder.append(newLine);
        builder.append("Overall precision: ");
        builder.append(getOverallPrecision());
        builder.append(newLine);
        builder.append("Overal recall: ");
        builder.append(getOverallRecall());
        builder.append(newLine);
        builder.append(line);
        builder.append(newLine);
        builder.append("Label Values");
        getLabels().forEach(label ->{
            builder.append(newLine);
            builder.append(line);
            builder.append(newLine);
            builder.append("Label: ");
            builder.append(label);
            builder.append(newLine);
            builder.append("Precision: ");
            builder.append(getPrecision(label));
            builder.append(newLine);
            builder.append("Recall: ");
            builder.append(getRecall((label)));
            builder.append(newLine);
            builder.append("true positives: ");
            builder.append(labelTruePositives.get(label));
            builder.append(newLine);
            builder.append("false positives: ");
            builder.append(labelFalsePositives.get(label));
            builder.append(newLine);
            builder.append("true negatives: ");
            builder.append(labelTrueNegatives.get(label));
            builder.append(newLine);
            builder.append("false negatives: ");
            builder.append(labelFalseNegatives.get(label));
            builder.append(newLine);
        });
        return builder.toString();
    }

}
