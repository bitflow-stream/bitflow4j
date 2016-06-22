package metrics.algorithms.clustering;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class ClusterEvaluator {

    private final String UNCLASSIFIED_CLUSTER = "unclassified";
    private final Map<Integer, MOAStreamClusterer.ClusterCounters> clusterLabelMaps;
    private final double thresholdToClassifyCluster;
    private double overallPrecision;
    private Map<String, Double> labelPrecisionMap;

    public ClusterEvaluator(Map<Integer, MOAStreamClusterer.ClusterCounters> clusterLabelMaps, double thresholdToClassifyCluster) {
        this.clusterLabelMaps = clusterLabelMaps;
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
        this.evaluate();
    }

    private void evaluate() {
        //Check which label is most important
        Map<Integer, String> classifiedClusters = new HashMap<>();
        clusterLabelMaps.keySet().stream()
                .forEach((clusterId) -> {
                    MOAStreamClusterer.ClusterCounters clusterCounts = clusterLabelMaps.get(clusterId);
                    String bestLabel = clusterCounts.counters.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                            .getValue() ? 1
                                    : -1).get().getKey();
                    double bestChance = (double) clusterCounts.counters.get(bestLabel) / (double) clusterCounts.total;
                    if (bestChance >= thresholdToClassifyCluster) {
                        classifiedClusters.put(clusterId, bestLabel);
                    } else {
                        classifiedClusters.put(clusterId, UNCLASSIFIED_CLUSTER);
                    }
                });

        //Precision for each label
        Map<String, Integer> labelPositiveCountMap = new HashMap<>();
        Map<String, Integer> labelNegativeCountMap = new HashMap<>();
        classifiedClusters.keySet().stream()
                .forEach((clusterId) -> {
                    if (clusterId != -1 && !classifiedClusters.get(clusterId).equals(UNCLASSIFIED_CLUSTER)) {
                        if (labelPositiveCountMap.containsKey(classifiedClusters.get(clusterId))) {
                            Integer positiveCount = clusterLabelMaps.get(clusterId).counters.get(classifiedClusters.get(clusterId));
                            labelPositiveCountMap.put(classifiedClusters.get(clusterId), labelPositiveCountMap
                                    .get(classifiedClusters.get(clusterId)) + positiveCount);
                        } else {
                            Integer positiveCount = clusterLabelMaps.get(clusterId).counters.get(classifiedClusters.get(clusterId));
                            labelPositiveCountMap.put(classifiedClusters.get(clusterId), positiveCount);
                        }
                        //Add to negative count
                        clusterLabelMaps.get(clusterId).counters.entrySet().stream()
                                .forEach((labelCounts) -> {
                                    if (!labelCounts.getKey().equals(classifiedClusters.get(clusterId))) {
                                        if (labelNegativeCountMap.containsKey(labelCounts.getKey())) {
                                            Integer negativeCount = labelCounts.getValue();
                                            labelNegativeCountMap.put(labelCounts.getKey(), labelNegativeCountMap
                                                    .get(classifiedClusters.get(clusterId)) + negativeCount);
                                        } else {
                                            Integer negativeCount = labelCounts.getValue();
                                            labelNegativeCountMap.put(labelCounts.getKey(), negativeCount);
                                        }
                                    }
                                });
                    } else if (classifiedClusters.get(clusterId).equals(UNCLASSIFIED_CLUSTER) || clusterId == -1) {
                        //Add to negative count
                        clusterLabelMaps.get(clusterId).counters.entrySet().stream()
                                .forEach((labelCounts) -> {
                                    if (labelNegativeCountMap.containsKey(labelCounts.getKey())) {
                                        Integer negativeCount = labelCounts.getValue();
                                        labelNegativeCountMap.put(labelCounts.getKey(), labelNegativeCountMap
                                                .get(classifiedClusters.get(clusterId)) + negativeCount);
                                    } else {
                                        Integer negativeCount = labelCounts.getValue();
                                        labelNegativeCountMap.put(labelCounts.getKey(), negativeCount);
                                    }
                                });
                    }
                });
        labelPrecisionMap = new HashMap<>();
        int positiveOverallCout = 0;
        int totalOverallCount = 0;
        for (Map.Entry<String, Integer> positiveCounts : labelPositiveCountMap.entrySet()) {
            int positiveCount = positiveCounts.getValue();
            positiveOverallCout += positiveCount;
            int negativeCount = 0;
            if (labelNegativeCountMap.containsKey(positiveCounts.getKey())) {
                negativeCount = labelNegativeCountMap.get(positiveCounts.getKey());
            }
            int totalCount = positiveCount + negativeCount;
            totalOverallCount += totalCount;
            labelPrecisionMap.put(positiveCounts.getKey(), (double) positiveCount / (double) totalCount);
        }
        //Overall Precision
        overallPrecision = (double) positiveOverallCout / (double) totalOverallCount;
        //TODO: UNCLASSIFIED_CLUSTER and unclassified samples or check recall
    }

    public Map<Integer, MOAStreamClusterer.ClusterCounters> getClusterLabelMaps() {
        return clusterLabelMaps;
    }

    public double getThresholdToClassifyCluster() {
        return thresholdToClassifyCluster;
    }

    public double getOverallPrecision() {
        return overallPrecision;
    }

    public Map<String, Double> getLabelPrecisionMap() {
        return labelPrecisionMap;
    }

}
