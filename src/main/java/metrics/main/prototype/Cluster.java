package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.OnlineFeatureStandardizer;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        public SampleOutput(Set<String> allClasses, String hostname, MOAStreamClusterer<AbstractClusterer> clusterer) {
            super(fillAllClasses(allClasses), hostname);
            this.clusterer = clusterer;
        }

        private static Set<String> fillAllClasses(Set<String> allClasses) {
            // allClasses.add(MOAStreamClusterer.UNKNOWN_LABEL);
            allClasses.add(OTHER_LABEL);
            return allClasses;
        }

        @SuppressWarnings("StringEquality")
        protected Sample executeSample(Sample sample) throws IOException {
            MOAStreamClusterer.ClusterCounters counts = clusterer.stringLabelMaps.get(sample.getLabel());
            if (counts == null) {
                System.err.println("Warning: Failed to get cluster counts for sample with label " + sample.getLabel());
                return null;
            }

            double values[] = new double[header.header.length];
            for (Map.Entry<String, Integer> entry : counts.counters.entrySet()) {
                if (entry.getKey() == MOAStreamClusterer.UNKNOWN_LABEL) {
                    continue;
                }
                double probability = entry.getValue().doubleValue() / (double) counts.total;
                int index;
                if (fieldIndices.containsKey(entry.getKey())) {
                    index = fieldIndices.get(entry.getKey());
                } else {
                    index = fieldIndices.get(OTHER_LABEL);
                }
                values[index] = probability;
            }

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
