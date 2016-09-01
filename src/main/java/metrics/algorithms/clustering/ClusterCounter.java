package metrics.algorithms.clustering;

import java.util.*;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ClusterCounter {

    private final Set<String> allLabels = new TreeSet<>();
    private final Map<Integer, ClusterCounters> clusterIdToCounters;
    private final double thresholdToClassifyCluster;

    private static final Map<String, Double> empty_map = new HashMap<>();

    static {
        empty_map.put(ClusterConstants.NOISE_CLUSTER, 1.0);
    }

    public ClusterCounter(double thresholdToClassifyCluster) {
        this.thresholdToClassifyCluster = thresholdToClassifyCluster;
        clusterIdToCounters = new HashMap<>();
    }
    
    public void increment(int id, String label) {
        allLabels.add(label);
        if (clusterIdToCounters.containsKey(id)) {
            clusterIdToCounters.get(id).increment(label);
        } else {
            ClusterCounters cc = new ClusterCounters(id, thresholdToClassifyCluster);
            clusterIdToCounters.put(id, cc);
            cc.increment(label);
        }
    }
    
    public String calculateLabel(int id){
        if(id == -1) return ClusterConstants.NOISE_CLUSTER;
        ClusterCounters counters = clusterIdToCounters.get(id);
        if (counters == null) {
            System.out.println("returning unclassified in ClusterCounter");
            return ClusterConstants.UNCLASSIFIED_CLUSTER;
        } else {
            return counters.calculateLabel();
        }
    }

    public void reset() {
        clusterIdToCounters.clear();
    }



    public Map<String, Double> getLabelInclusionProbability(int id) {
        ClusterCounters counters = clusterIdToCounters.get(id);
        if (counters == null) {
            return empty_map;
        } else {
            return counters.getLabelInclusionProbability();
        }
    }

    public Collection<String> getAllLabels() {
        return allLabels;
    }

}
