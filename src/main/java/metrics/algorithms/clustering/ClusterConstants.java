package metrics.algorithms.clustering;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ClusterConstants {
    public static final String UNCLASSIFIED_CLUSTER = "unclassified";
    public static final String UNKNOWN_LABEL = "unknown";
    public static final String ORIGINAL_LABEL_TAG = "cls-input";
    public static final String CLUSTER_TAG = "cluster";

    // Prefix for meta metric names
    public static final String INC_PROB_PREFIX = "_prob_";

    /**
     * label for a noise cluster
     */
    public final static String NOISE_CLUSTER = "noise";
    public static final String BUFFERED_SAMPLE_TAG = "buffered";
    public static final String TRAINING_TAG = "trained";
    public static final String EXPECTED_PREDICTION_TAG = "expected-cls";
    // TODO move prefix
    public static String DISTANCE_PREFIX = "_distance_";
    public static String BUFFERED_LABEL = "buffered";
}
