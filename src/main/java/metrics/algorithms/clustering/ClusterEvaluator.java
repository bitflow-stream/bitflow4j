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
        //Find labels, which represent a given cluster best (better than given threshold-thresholdToClassifyCluster). If no best label was found, it is labeld as unclassified_cluster.
        Map<Integer, String> classifiedClusters = new HashMap<>();
        clusterLabelMaps.keySet().stream().forEach((clusterId) -> {
            MOAStreamClusterer.ClusterCounters clusterCounts = clusterLabelMaps.get(clusterId);
            String bestLabel = clusterCounts.counters.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                    .getValue() ? 1 : -1).get().getKey();
            double bestChance = (double) clusterCounts.counters.get(bestLabel) / (double) clusterCounts.total;
            if (bestChance >= thresholdToClassifyCluster) {
                classifiedClusters.put(clusterId, bestLabel);
            } else {
                classifiedClusters.put(clusterId, UNCLASSIFIED_CLUSTER);
            }
        });

        //Count for each label the correct recommended sample counts by counting all representative cluster label counts.
        Map<String, Integer> labelPositiveCountMap = new HashMap<>();
        classifiedClusters.keySet().stream().forEach((clusterId) -> {
            //Cluster ID == -1 must be skipped, because those are outlier and not classified in any cluster by the online clustering algorithm. Also unclassified_clusters need to be skipped, because we do not know, which label we have as representative here for positive countings.
            if (clusterId != -1 && !classifiedClusters.get(clusterId).equals(UNCLASSIFIED_CLUSTER)) {
                //Add counts to labelPositiveCountMap
                if (labelPositiveCountMap.containsKey(classifiedClusters.get(clusterId))) {
                    Integer positiveCount = clusterLabelMaps.get(clusterId).counters.get(classifiedClusters.get(clusterId));
                    labelPositiveCountMap.put(classifiedClusters.get(clusterId), labelPositiveCountMap
                            .get(classifiedClusters.get(clusterId)) + positiveCount);
                } else {
                    Integer positiveCount = clusterLabelMaps.get(clusterId).counters.get(classifiedClusters.get(clusterId));
                    labelPositiveCountMap.put(classifiedClusters.get(clusterId), positiveCount);
                }
            }
        });
        //Negative counts are counts, where samples with other labels are included in any cluster, which has another labeling (classification) for samples in a given cluster. Such counts are sumed up with respect to each given label. 
        Map<String, Integer> labelNegativeCountMap = new HashMap<>();
        clusterLabelMaps.keySet().stream().forEach((clusterId) -> {
            for (Map.Entry<String, Integer> labelCounts : clusterLabelMaps.get(clusterId).counters.entrySet()) {
                //Check cluster with ID == -1, because it contains outliers, which where not classified by the clusterer. Also check all clusters, where we are unsure which label this cluster represents. And check for all other labels accept the representative cluster-label, because such labels appear in a not suited cluster.
                if (classifiedClusters.get(clusterId).equals(UNCLASSIFIED_CLUSTER) || 
                        clusterId == -1 || 
                        !labelCounts.getKey().equals(classifiedClusters.get(clusterId))) {
                    if (labelNegativeCountMap.containsKey(labelCounts.getKey())) {
                        Integer negativeCount = labelCounts.getValue();
                        labelNegativeCountMap.put(labelCounts.getKey(), labelNegativeCountMap
                                .get(labelCounts.getKey()) + negativeCount);
                    } else {
                        Integer negativeCount = labelCounts.getValue();
                        labelNegativeCountMap.put(labelCounts.getKey(), negativeCount);
                    }
                }
            }
        });

        //Now the precision per label is calculated. The precision for a single label states the percentage of correct recommended samples in contrats to all incoming samples of the given label.
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
        //Overall Precision states the percentage of all correct classified samples in contrast to all samples.
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
