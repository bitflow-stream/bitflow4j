package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.OnlineFeatureStandardizer;
import metrics.algorithms.clustering.ClusterLabelingAlgorithm;
import metrics.algorithms.clustering.ExternalClusterer;
import metrics.algorithms.clustering.LabelAggregatorAlgorithm;
import metrics.algorithms.clustering.MOAStreamClusterer;
import metrics.algorithms.evaluation.MOAStreamEvaluator;
import metrics.io.MetricPrinter;
import metrics.io.fork.TwoWayFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;

/**
 * Created by anton on 6/21/16.
 */
public class Cluster {

    private static final double classifiedClusterThreshold = 0.1;
    private static final long labelAggregationWindow = 2; // Seconds

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
        MOAStreamClusterer<AbstractClusterer> moaClusterer = new MOAStreamClusterer<>(clusterer);

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
                                    .step(new ClusterLabelingAlgorithm(classifiedClusterThreshold, true))
                                    .step(new LabelAggregatorAlgorithm(labelAggregationWindow))
                                    .step(new MOAStreamEvaluator(1, false, true))
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
