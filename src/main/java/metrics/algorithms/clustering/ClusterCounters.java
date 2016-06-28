package metrics.algorithms.clustering;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ClusterCounters {
    private Map<String, Integer> counters = new HashMap<>();
    private int clusterId;
    private long total;
    private double thresholdToClassifyCluster;

    public ClusterCounters(int clusterId, double thresholdToClassifyCluster) {
        this.clusterId = clusterId;
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
    }

    public long getTotal() {

        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Map<String, Integer> getCounters() {

        return counters;
    }

    public int getClusterId() {

        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    @SuppressWarnings("StringEquality")
    public String increment(String label) {
        total++;
        if (counters.containsKey(label)) {
            counters.put(label, counters.get(label) + 1);
        } else {
            counters.put(label, 1);
        }
        return thresholdToClassifyCluster < 0 ? null : calculateLabel();
    }

    private String calculateLabel() {
        if (clusterId != -1) {
            //TODO: allow multilabel clusters
            String bestLabel = counters.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                    .getValue() ? 1 : -1).get().getKey();
            //the chance for the best label
            double bestChance = (double) counters.get(bestLabel) / (double) total;
            //check quality requirement and label
            if (bestChance >= thresholdToClassifyCluster) {
                return bestLabel;
            } else {
                return ClusterConstants.UNCLASSIFIED_CLUSTER;
            }
        } else {
            //if clusterId == -1, its noise
            return ClusterConstants.NOISE_CLUSTER;
        }
    }
}
