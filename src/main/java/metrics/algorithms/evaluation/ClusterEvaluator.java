package metrics.algorithms.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.algorithms.clustering.ClusterCounters;

/**
 * @author fschmidt, mbyfield
 *         This class provides basic evaluation methods for the MOAStreamClusterer.
 */
public class ClusterEvaluator {
    /**
     * Label for an unclassified cluster.
     */
    /**
     * Static String declarations
     */
    private final static String LINE = "----------------------------------------------";
    private final static String HASH = "##############################################";
    private final static String NEW_LINE = System.getProperty("line.separator");
    /**
     * Map of clusterIndexes to cluster counters
     */
    private final Map<Integer, ClusterCounters> clusterLabelMaps;
    /**
     * The provided threshold to label a cluster
     */
    private final double thresholdToClassifyCluster;
    /**
     * maps for tp, fp, tn, fn per label
     */
    private Map<String, Integer> labelFalsePositives, labelTruePositives, labelFalseNegatives, labelTrueNegatives;
    /**
     * map holding the label assignment for each cluster id
     */
    private Map<Integer, String> classifiedClusters;
    /**
     * sum of all tp / fp / tn / fn over all labels
     */
    private long truePositives, trueNegatives, falseNegatives, falsePositives;
    /**
     * sum of all points provided to the evaluator
     */
    private int numberOfPoints;

    /**
     * Constructor. When called, the evaluation on the provided cluster data is started immediately. When the constructor returns, the evaluation is finished.
     *
     * @param clusterLabelMaps           A Map of clusterIndexes to cluster counters. Used for the evaluation. Must ot be null or empty.
     * @param thresholdToClassifyCluster The threshold used to label a cluster. Must be between 0 and 1. A cluster must fulfill the following equation or it will be marked as unclassified: threshodToClassifyCluster <
     * @param numberOfPoints             The sum of points for all MOAStreamClusterer.ClusterCounters in the clusterLabelMaps. Must be > 0. If the wrong number of points is provided, the evaluation might be incorrect.
     * @throws IllegalArgumentException If number of points is 0 or clusterLabelMas isEmpty or the nullpointer.
     */
    public ClusterEvaluator(Map<Integer, ClusterCounters> clusterLabelMaps, double thresholdToClassifyCluster, int numberOfPoints) throws IllegalArgumentException {
        /** integrity check */
        if (thresholdToClassifyCluster < 0 || thresholdToClassifyCluster > 1)
            throw new IllegalArgumentException("thresholdToClassify should be between 0 and 1, but " + thresholdToClassifyCluster + " was provided!");
        if (clusterLabelMaps == null || clusterLabelMaps.isEmpty() || numberOfPoints == 0)
            throw new IllegalArgumentException("The cannot evaluate empty cluster.");
        //field assignment
        this.clusterLabelMaps = clusterLabelMaps;
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
        this.numberOfPoints = numberOfPoints;
        initEmptyFields();
        //start evaluation
        this.evaluate();
    }

    /*
     * Helper method for the evaluation. Contains all steps required for evaluation. private fields must be set and initialized.
     * Sets the fields: truePositives, trueNegatives, falseNegatives, falsePositives, classifiedClusters, labelFalsePositives,
     * labelTruePositives, labelFalseNegatives and labelTrueNegatives
     */
    private void evaluate() {
        //find labels
        calculateAllBestLabels();
        //fill label maps with tp, fp, etc.
        calculateLabelQuality();
        //sum up results from maps
        calculateOverallQuality();
    }

    /**
     * Sums up results from maps. Fills the fields truePositives, trueNegatives, falseNegatives and falsePositives.
     */
    private void calculateOverallQuality() {
        //building sums over the values for each label
        truePositives = labelTruePositives.values().stream().mapToInt(i -> i.intValue()).sum();
        trueNegatives = labelTrueNegatives.values().stream().mapToInt(i -> i.intValue()).sum();
        falsePositives = labelFalsePositives.values().stream().mapToInt(i -> i.intValue()).sum();
        falseNegatives = labelFalseNegatives.values().stream().mapToInt(i -> i.intValue()).sum();
    }

    /**
     * Calculates tp, fp, etc. for all labels and stores the results in the corresponding fields: labelFalsePositives, labelTruePositives, labelFalseNegatives and labelTrueNegatives.
     */
    private void calculateLabelQuality() {
        //fill maps with labels as keys and initial 0's as value
        initLabelMaps();
        //loop over all clusters
        classifiedClusters.entrySet().forEach((labeledClusterId) -> {
            //id of current cluster
            int clusterId = labeledClusterId.getKey();
            //label of current cluster
            String clusterLabel = labeledClusterId.getValue();
            //iterate over all counters of the current cluster (Map<String, Integer>; Label,pointcount)
            clusterLabelMaps.get(clusterId).getCounters().entrySet().forEach(countersEntry -> {
                //current Label of points
                String pointLabel = countersEntry.getKey();
                //current count
                int pointCount = countersEntry.getValue();
                //all points with the same label as the cluster are true positives for this label.
                if (pointLabel.equals(clusterLabel)) {
                    //add pointcount to map
                    labelTruePositives.replace(clusterLabel, labelTruePositives
                            .get(clusterLabel) + pointCount);
                } else {
                    //if its not noise or an unclassified cluster, all other points in this cluster are false positives for this label.
                    //TODO: double check, that including noise and unclassified clusters wont change evaluation result
                    if ((!pointLabel.equals(ClusterConstants.NOISE_CLUSTER)) && !pointLabel.equals(ClusterConstants.UNCLASSIFIED_CLUSTER)) {
                        //add pointcount to map
                        labelFalsePositives.replace(clusterLabel, labelFalsePositives.containsKey(clusterLabel) ? labelFalsePositives
                                .get(clusterLabel) + pointCount : pointCount);
                    }
                    //all points with the wrong label are FalseNegatives for this label, because they wont be represented in any other cluster with the correct label,
                    // add pointcount to map
                    labelFalseNegatives.replace(pointLabel, labelFalseNegatives.containsKey(clusterLabel) ? labelFalseNegatives
                            .get(clusterLabel) + pointCount : pointCount);
                }//end else
            });//end loop over clustercounters
        });//end loop over clusters
        //tn = numberOfPoints - (tp + fp + fn)
        //iterate over all labels and calculate tn, add count to map
        labelTruePositives.entrySet().forEach(entry -> labelTrueNegatives.replace(entry.getKey(), numberOfPoints - (entry.getValue() + labelFalseNegatives.get(entry.getKey()) + labelFalsePositives.get(entry.getKey()))));
    }

    /**
     * Fill maps with labels as keys and initial 0's as value.
     */
    private void initLabelMaps() {
        //iterate over disting labels
        classifiedClusters.values().stream().distinct().forEach(label -> {
            labelFalseNegatives.put(label, 0);
            labelFalsePositives.put(label, 0);
            labelTruePositives.put(label, 0);
            labelTrueNegatives.put(label, 0);
        });
    }

    /**
     * initializes empty fields
     */
    private void initEmptyFields() {
        labelFalseNegatives = new HashMap<>();
        labelFalsePositives = new HashMap<>();
        labelTrueNegatives = new HashMap<>();
        labelTruePositives = new HashMap<>();
        classifiedClusters = new HashMap<>();
    }

    /**
     * Calculates the best label for all clusters, by finding the label that represents most points. Uses local field threshodToClassifyCluster.
     * Clusters that acnnot be classified are marked as "unclassified", the noise cluster gets the label "noise"
     * Can currently not find equal labels.
     */
    private void calculateAllBestLabels() {
        //iterate over cluster ids
        clusterLabelMaps.keySet().stream().forEach((clusterId) -> {
            //not noise cluster
            if (clusterId != -1) {
                //counters for the current cluster
                ClusterCounters clusterCounts = clusterLabelMaps.get(clusterId);
                //iterate over counters and find best label by using the max of each labels count
                //TODO: allow multilabel clusters
                String bestLabel = clusterCounts.getCounters().entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                        .getValue() ? 1 : -1).get().getKey();
                //the chance for the best label
                double bestChance = (double) clusterCounts.getCounters().get(bestLabel) / (double) clusterCounts.getTotal();
                //check quality requirement and label
                if (bestChance >= thresholdToClassifyCluster) {
                    classifiedClusters.put(clusterId, bestLabel);
                } else {
                    classifiedClusters.put(clusterId, ClusterConstants.UNCLASSIFIED_CLUSTER);
                }
            } else {
                //if clusterId == -1, its noise
                classifiedClusters.put(clusterId, ClusterConstants.NOISE_CLUSTER);
            }
        });//end iteration over clusterids
    }

    /**
     * Getter for the provided Map of cluster id's to ClusterCounters.
     * @return The provided map. Mutable, not threadsafe.
     */
    public Map<Integer, ClusterCounters> getClusterLabelMaps() {
        return clusterLabelMaps;
    }

    /**
     * Getter for the provided threshodToClassifyCluster.
     * @return The provided threshodToClassifyCluster.
     */
    public double getThresholdToClassifyCluster() {
        return thresholdToClassifyCluster;
    }

    /**
     * Calculate the overall precision for the Clustering
     * @return the precision of the clustering
     */
    public double getOverallPrecision() {
        return (double) truePositives / (truePositives + falsePositives);
    }
    /**
     * Calculate the overall recall for the Clustering
     * @return the recall of the clustering
     */
    public double getOverallRecall() {
        return (double) truePositives / (truePositives + falseNegatives);
    }
    /**
     * Calculate the recall for a label
     * @param label the label
     * @return the recall of the label
     */
    public double getRecall(String label) {
        return (double) labelTruePositives.get(label) / (labelTruePositives.get(label) + labelFalseNegatives.get(label));
    }

    /**
     * Calculate the precision for a label
     * @param label the label
     * @return the precision of the label
     */
    public double getPrecision(String label) {
        return (double) labelTruePositives.get(label) / (labelTruePositives.get(label) + labelFalsePositives.get(label));
    }

    /**
     * Gets a Set containing all Labels
     * @return A Set containing all Labels
     */
    public Set<String> getLabels() {
        return labelTruePositives.keySet();
    }

    @Override
    /**
     * A String representation of the evaluation for log or console output.
     */
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(HASH);
        builder.append(NEW_LINE);
        builder.append("CLUSTER EVALUATION");
        builder.append(NEW_LINE);
        builder.append("Number of Points: ");
        builder.append(numberOfPoints);
        builder.append(NEW_LINE);
        builder.append("Overall precision: ");
        builder.append(getOverallPrecision());
        builder.append(NEW_LINE);
        builder.append("Overal recall: ");
        builder.append(getOverallRecall());
        builder.append(NEW_LINE);
        builder.append(LINE);
        builder.append(NEW_LINE);
        builder.append("Label Values");
        labelsToString(builder);
        return builder.toString();
    }

    private void labelsToString(StringBuilder builder) {
        getLabels().forEach(label -> {
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
            builder.append(labelTruePositives.get(label));
            builder.append(NEW_LINE);
            builder.append("false positives: ");
            builder.append(labelFalsePositives.get(label));
            builder.append(NEW_LINE);
            builder.append("true negatives: ");
            builder.append(labelTrueNegatives.get(label));
            builder.append(NEW_LINE);
            builder.append("false negatives: ");
            builder.append(labelFalseNegatives.get(label));
            builder.append(NEW_LINE);
        });
    }

}
