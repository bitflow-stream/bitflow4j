package metrics.algorithms.clustering;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ClusterCounter {

    private Map<Integer, ClusterCounters> clusterIdToCounters;
    private double thresholdToClassifyCluster;

    public ClusterCounter(double thresholdToClassifyCluster) {
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
        clusterIdToCounters = new HashMap<>();
    }

    public String increment(int id, String label) {
        if (clusterIdToCounters.containsKey(id)) return clusterIdToCounters.get(id).increment(label);
        else {
            ClusterCounters cc = new ClusterCounters(id, thresholdToClassifyCluster);
            clusterIdToCounters.put(id, cc);
            return cc.increment(label);
        }
    }
}
