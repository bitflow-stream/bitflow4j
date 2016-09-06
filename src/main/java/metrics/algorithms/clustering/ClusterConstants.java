package metrics.algorithms.clustering;

/**
 * This class holds all constants used for clustering (e.g. tags for cluster id, and labels).
 */
public class ClusterConstants {

    // ======================
    // ==== Metric names ====
    // ======================

    // Prefix for metrics containing the probability for the sample to be part of a given label.
    // E.g. _prob_idle contains a value between 0 and 1, stating how likely the sample is "idle".
    public static final String INC_PROB_PREFIX = "_prob_";

    // Prefix for metrics giving the distance of the sample to the closest cluster.
    // E.g. _distance_cpu gives the distance on the "cpu" metric to the closest cluster.
    // _distance_overall gives the euklidean distance to the closest cluster.
    // Values of < 0 mean the Sample is actually inside a cluster.
    public static String DISTANCE_PREFIX = "_distance_";

    // Optionally used by StreamEvaluator algorithm, contains the historical precision of the
    // clustering/classification algorithm in the pipeline.
    public static final String OVERALL_PRECISION_METRIC = "_overall_precision_";

    // ===================
    // ==== Tag names ====
    // ===================

    // If an incoming Sample has a cls tag set, and the cls tag is overwritten, the original
    // label will be stored with this tag.
    public static final String ORIGINAL_LABEL_TAG = "cls-input";

    // Tag name containing the cluster id of an outgoing sample.
    public static final String CLUSTER_TAG = "cluster";

    // Used by ExtendedStreamEvaluator to calculate recall/precision. Contains the label that should be
    // predicted for the given sample.
    public static final String EXPECTED_PREDICTION_TAG = "expected-cls";

    // ================
    // ==== Labels ====
    // ================

    // Label for samples that are clustered as noise
    public final static String NOISE_CLUSTER = "noise";

    // Label for samples in clusters that don't have enough information or where the distribution of incoming
    // labels is too even to pick a clear dominating label
    public static final String UNCLASSIFIED_CLUSTER = "unclassified";

}
