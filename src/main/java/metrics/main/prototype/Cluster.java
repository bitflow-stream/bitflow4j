package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.Algorithm;
import metrics.algorithms.OnlineFeatureStandardizer;
import metrics.algorithms.clustering.ClusterLabelingAlgorithm;
import metrics.algorithms.clustering.ExternalClusterer;
import metrics.algorithms.clustering.LabelAggregatorAlgorithm;
import metrics.algorithms.clustering.MOAStreamClusterer;
import metrics.algorithms.evaluation.MOAStreamAnomalyDetectionEvaluator;
import metrics.io.MetricPrinter;
import metrics.io.fork.TwoWayFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 6/21/16.
 */
public class Cluster {

    private static final double classifiedClusterThreshold = 0.1;
    private static final long labelAggregationWindow = 4000; // Milliseconds
    private static final int labelAggregationWindow_number = 20;

    public static void main(String[] args) throws IOException {
        if (args.length != 7) {
            System.err.println("Parameters: <receive-port> <trained model file> <target-host> <target-port> <local hostname> <filter> <num_clusters>");
            return;
        }
        int receivePort = Integer.parseInt(args[0]);
        TrainedDataModel model = Analyse.getDataModel(args[1]);
        String targetHost = args[2];
        int targetPort = Integer.parseInt(args[3]);
        String hostname = args[4];
        String filter = args[5];
        int num_clusters = Integer.valueOf(args[6]);
        Algorithm filterAlgo = Train.getFilter(filter);

        AbstractClusterer clusterer = ExternalClusterer.BICO.newInstance();
        Set<String> trainedLabels = new HashSet<>(Arrays.asList(new String[] { "idle", "load" }));
        MOAStreamClusterer<AbstractClusterer> moaClusterer = new MOAStreamClusterer<>(clusterer, trainedLabels, num_clusters);

        new AlgorithmPipeline(receivePort, Analyse.TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }
                            p
                                    .step(filterAlgo)
                                    .step(new OnlineFeatureStandardizer(model.averages, model.stddevs))
                                    // .step(new OnlineAutoFeatureStandardizer())
                                    .step(moaClusterer)
                                    .step(new ClusterLabelingAlgorithm(classifiedClusterThreshold, true, true, trainedLabels))
                                    // .step(new LabelAggregatorAlgorithm(labelAggregationWindow))
                                    .step(new LabelAggregatorAlgorithm(labelAggregationWindow_number))
                                    // .step(new MOAStreamEvaluator(1, false, true))
                                    .step(new MOAStreamAnomalyDetectionEvaluator(1, false, true, trainedLabels, "normal"))
                                    .step(new SampleOutput(hostname))
                                    .fork(new TwoWayFork(),
                                            (type, out) -> out.output(
                                                    type == TwoWayFork.ForkType.Primary ?
                                                            new MetricPrinter(AlgorithmPipeline.getMarshaller(Analyse.CONSOLE_OUTPUT)) :
                                                            new TcpMetricsOutput(AlgorithmPipeline.getMarshaller(Analyse.TCP_OUTPUT_FORMAT), targetHost, targetPort)));
                        })
                .runAndWait();
    }

    private static class SampleOutput extends AbstractAlgorithm {

        private final String hostname;

        public SampleOutput(String hostname) {
            this.hostname = hostname;
        }

        protected Sample executeSample(Sample sample) throws IOException {
            Sample output = new Sample(sample);
            output.setTag(SampleAnalysisOutput.TAG_HOSTNAME, hostname);
            return output;
        }

        @Override
        public String toString() {
            return "hostname tagger";
        }
    }

}
