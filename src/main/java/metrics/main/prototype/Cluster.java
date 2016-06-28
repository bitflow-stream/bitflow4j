package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.OnlineFeatureStandardizer;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.algorithms.clustering.ClusterCounters;
import metrics.algorithms.clustering.ExternalClusterer;
import metrics.algorithms.clustering.MOAStreamClusterer;
import metrics.io.MetricPrinter;
import metrics.io.fork.TwoWayFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by anton on 6/21/16.
 */
public class Cluster {

    public static void main(String[] args) throws IOException {
        if (args.length != 5) {
            System.err.println("Parameters: <receive-port> <trained model file> <target-host> <target-port> <local hostname>");
            return;
        }
        int receivePort = Integer.parseInt(args[0]);
        TrainedDataModel model = Analyse.getDataModel(args[1]);
        String targetHost = args[2];
        int targetPort = Integer.parseInt(args[3]);
        String hostname = args[4];

        AbstractClusterer clusterer = ExternalClusterer.BICO.newInstance();
        MOAStreamClusterer<AbstractClusterer> moaClusterer = new MOAStreamClusterer<>(clusterer, 0);

        new AlgorithmPipeline(receivePort, Analyse.TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }
                            p
                                    .step(new OnlineFeatureStandardizer(model.averages, model.stddevs))
                                    .step(moaClusterer)
                                    .step(new SampleOutput(new HashSet<>(model.allClasses), hostname, moaClusterer))
                                    .fork(new TwoWayFork(),
                                            (type, out) -> out.output(
                                                    type == TwoWayFork.ForkType.Primary ?
                                                            new MetricPrinter(AlgorithmPipeline.getMarshaller(Analyse.CONSOLE_OUTPUT)) :
                                                            new TcpMetricsOutput(AlgorithmPipeline.getMarshaller(Analyse.TCP_OUTPUT_FORMAT), targetHost, targetPort)));
                        })
                .runAndWait();
    }

    private static class SampleOutput extends SampleAnalysisOutput {

        private static final String OTHER_LABEL = "other";
        private final MOAStreamClusterer<AbstractClusterer> clusterer;

        public SampleOutput(Collection<String> allClasses, String hostname, MOAStreamClusterer<AbstractClusterer> clusterer) {
            super(fillAllClasses(allClasses), hostname);
            this.clusterer = clusterer;
        }

        private static Collection<String> fillAllClasses(Collection<String> allClasses) {
            ArrayList<String> classes = new ArrayList<>(allClasses);
            classes.sort(String.CASE_INSENSITIVE_ORDER);
            classes.add(OTHER_LABEL);
            classes.add(ClusterConstants.UNKNOWN_LABEL);
            return classes;
        }

        @SuppressWarnings("StringEquality")
        protected Sample executeSample(Sample sample) throws IOException {
            String cluster = sample.getTag(ClusterConstants.CLUSTER_TAG);
            ClusterCounters counts = clusterer.stringLabelMaps.get(cluster);
            if (counts == null) {
                System.err.println("Warning: Failed to get cluster counts for sample with label " + cluster);
                return null;
            }

            double values[] = new double[header.header.length];
            for (Map.Entry<String, Integer> entry : counts.getCounters().entrySet()) {
                if (entry.getKey() == ClusterConstants.UNKNOWN_LABEL) {
                    continue;
                }
                double probability = entry.getValue().doubleValue() / (double) counts.getTotal();
                int index;
                if (fieldIndices.containsKey(entry.getKey())) {
                    index = fieldIndices.get(entry.getKey());
                } else {
                    index = fieldIndices.get(OTHER_LABEL);
                }
                values[index] = probability;
            }
            double unknownPercentage = 0;
            Integer unknowns = counts.getCounters().get(ClusterConstants.UNKNOWN_LABEL);
            if (unknowns != null) {
                unknownPercentage = unknowns.doubleValue() / (double) (counts.getTotal() + unknowns);
            }
            values[fieldIndices.get(ClusterConstants.UNKNOWN_LABEL)] = unknownPercentage;

            Map<String, String> tags = sample.getTags();
            tags.put(SampleAnalysisOutput.TAG_HOSTNAME, hostname);
            return new Sample(header, values, sample.getTimestamp(), tags);
        }

        @Override
        public String toString() {
            return "clustered sample output";
        }
    }

}
