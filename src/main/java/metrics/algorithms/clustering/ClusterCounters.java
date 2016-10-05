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

    public void increment(String label) {
        total++;
        if (counters.containsKey(label)) {
            counters.put(label, counters.get(label) + 1);
        } else {
            counters.put(label, 1);
        }
    }

    public String calculateLabel() {
        if (clusterId == -1) return ClusterConstants.NOISE_CLUSTER;

        //TODO: make decision on equal probabilities final
        String bestLabel = counters.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2
                .getValue() ? 1 : -1).get().getKey();
        //the chance for the best label
        double bestChance = (double) counters.get(bestLabel) / (double) total;
        //check quality requirement and label
        if (bestChance >= thresholdToClassifyCluster) {
            return bestLabel;
        } else {
            System.out.println("returning unclassified in ClusterCounter");
            return ClusterConstants.UNCLASSIFIED_CLUSTER;
        }
    }
    
    public Map<String, Double> getLabelInclusionProbability(){
        Map<String, Double> inclusionProbability = new HashMap<>();
        counters.keySet().stream()
                .forEach((key) -> {
                    inclusionProbability.put(key, (double)counters.get(key)/(double)total);
        });
        return inclusionProbability;
    }
}
