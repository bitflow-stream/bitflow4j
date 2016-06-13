package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by anton on 5/14/16.
 */
public class ClusterSummary extends AbstractAlgorithm {

    private static final int BIG_CLUSTER = 10;

    private static final Header overviewHeader = new Header(new String[]{
            "num_samples", "num_clusters", "avg_cluster_size",
            "largest_cluster", "small_clusters", "big_clusters"
    }, false);

    private TreeMap<String, ClusterStatistics> clusters = new TreeMap<>();
    private final boolean printDetails;
    private final boolean intermediateResults;

    public ClusterSummary(boolean printDetails, boolean intermediateResults) {
        this.printDetails = printDetails;
        this.intermediateResults = intermediateResults;
    }

    public ClusterSummary() {
        this(false, true);
    }

    protected Sample executeSample(Sample sample) {
        String label = sample.getLabel();
        ClusterStatistics stats = clusters.get(label);
        if (stats == null) {
            stats = new ClusterStatistics(label);
            clusters.put(label, stats);
        }
        stats.add(sample);
        return intermediateResults ? createOutput(sample.getTimestamp()) : null;
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        if (!intermediateResults)
            output.writeSample(createOutput(new Date()));
    }

    private Sample createOutput(Date timestamp) {
        return printDetails ? clusterDetails(timestamp) : clusterSummary(timestamp);
    }

    private Sample clusterSummary(Date timestamp) {
        int num_samples = 0;
        int num_clusters = 0;
        int largest_cluster = 0;
        int small_clusters = 0;
        int big_clusters = 0;
        for (ClusterStatistics cluster : clusters.values()) {
            num_samples += cluster.count;
            num_clusters++;
            if (cluster.count > largest_cluster) largest_cluster = cluster.count;
            if (cluster.count > BIG_CLUSTER)
                big_clusters++;
            else
                small_clusters++;
        }
        double avg_cluster_size = num_samples / num_clusters;
        double values[] = new double[]{
                num_samples, num_clusters, avg_cluster_size,
                largest_cluster, small_clusters, big_clusters
        };
        return new Sample(overviewHeader, values, timestamp);
    }

    private Sample clusterDetails(Date timestamp) {
        String fields[] = new String[clusters.size()];
        double values[] = new double[clusters.size()];
        int i = 0;
        for (String key : clusters.keySet()) {
            values[i] = (double) clusters.get(key).count;
            fields[i++] = clusters.get(key).name;
        }
        Header header = new Header(fields, false);
        return new Sample(header, values, timestamp);
    }

    private static class ClusterStatistics {
        int count = 0;
        final String name;

        public ClusterStatistics(String name) {
            this.name = name;
        }

        void add(Sample sample) {
            count++;
        }
    }

    @Override
    public String toString() {
        return "cluster summary";
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeBoolean(printDetails);
        hash.writeBoolean(intermediateResults);
    }

}
