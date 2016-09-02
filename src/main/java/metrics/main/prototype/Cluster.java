package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.Algorithm;
import metrics.algorithms.normalization.FeatureCalculationsAlgorithm;
import metrics.algorithms.normalization.OnlineAutoMinMaxScaler;
import metrics.algorithms.classification.SourceTrainingLabelingAlgorithm;
import metrics.algorithms.classification.SrcClsMapper;
import metrics.algorithms.clustering.BICOClusterer;
import metrics.algorithms.clustering.ClusterLabelingAlgorithm;
import metrics.algorithms.clustering.LabelAggregatorAlgorithm;
import metrics.algorithms.evaluation.MOAStreamEvaluator;
import metrics.io.MetricPrinter;
import metrics.io.fork.TwoWayFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.analysis.OpenStackSampleSplitter;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 6/21/16.
 */
public class Cluster {

    private static final double classifiedClusterThreshold = 0.1;
    private static final long labelAggregationWindow = 4000; // Milliseconds, must be declared as long

    public static void main(String[] args) throws IOException {
        if (args.length != 8) {
            System.err.println("Parameters: <receive-port> <feature ini file> <target-host> <target-port> <local hostname> <filter> <num_clusters> <num_cluster_features> <concept-change-enabled>");
            return;
        }
        int receivePort = Integer.parseInt(args[0]);
        String statsFile = args[1];
        FeatureStatistics stats = new FeatureStatistics(statsFile);
        String targetHost = args[2];
        int targetPort = Integer.parseInt(args[3]);
        String hostname = args[4];
        String filter = args[5];
        int num_clusters = Integer.valueOf(args[6]);
        int num_cluster_feature = Integer.valueOf(args[7]);
        boolean conceptChangeEnabled = Boolean.valueOf(args[8]);
        Algorithm filterAlgo = Train.getFilter(filter);

        //configure expected labels
        SrcClsMapper.useOriginal(true);
        Set<String> trainedLabels = new HashSet<>(Arrays.asList(new String[] { "idle", "load" }));
        BICOClusterer moaClusterer = new BICOClusterer(trainedLabels, true, num_cluster_feature , num_clusters, null).alwaysAddDistanceMetrics();
        ClusterLabelingAlgorithm labeling = new ClusterLabelingAlgorithm(classifiedClusterThreshold, true, false, trainedLabels);
        LabelAggregatorAlgorithm labelAggregatorAlgorithm = new LabelAggregatorAlgorithm(labelAggregationWindow);
        SourceTrainingLabelingAlgorithm sourceTrainingLabelingAlgorithm = new SourceTrainingLabelingAlgorithm();
        MOAStreamEvaluator evaluator = new MOAStreamEvaluator(1, false, true);
        HostnameTagger hostnameTagger = new HostnameTagger(hostname);

        OnlineAutoMinMaxScaler.ConceptChangeHandler conceptChangeHandler = (handler, feature) -> {
            OnlineAutoMinMaxScaler.Feature ft = handler.features.get(feature);
            if (!conceptChangeEnabled)
                System.err.print("IGNORED: ");
            System.err.println("New value range of " + feature + ": " + ft.reportedMin + " - " + ft.reportedMax);

            FeatureStatistics.Feature statsFt = stats.getFeature(feature);
            statsFt.min = ft.reportedMin;
            statsFt.max = ft.reportedMax;

            try {
                stats.writeFile(statsFile);
            } catch (IOException e) {
                System.err.println("Error storing new feature stats file: " + e.getMessage());
                e.printStackTrace();
            }

            if (conceptChangeEnabled) {
                hostnameTagger.samples_since_concept_change = 0; // Reset
                moaClusterer.resetClusters();
                labeling.resetCounters();
            }
            return conceptChangeEnabled;
        };

        FeatureCalculationsAlgorithm extraFeatures = getExtraFeatures();

        new AlgorithmPipeline(receivePort, Analyse.TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }
                            p
                                    .step(filterAlgo)
                                    .step(new OnlineAutoMinMaxScaler(0.5, conceptChangeHandler, stats))
                                    .step(extraFeatures)
                                    .step(sourceTrainingLabelingAlgorithm)
                                    .step(moaClusterer)
                                    .step(labeling)
                                    .step(labelAggregatorAlgorithm)
                                    .step(evaluator)
                                    .step(hostnameTagger)
                                    .fork(new TwoWayFork(),
                                            (type, out) -> out.output(
                                                    type == TwoWayFork.ForkType.Primary ?
                                                            new MetricPrinter(AlgorithmPipeline.getMarshaller(Analyse.CONSOLE_OUTPUT)) :
                                                            new TcpMetricsOutput(AlgorithmPipeline.getMarshaller(Analyse.TCP_OUTPUT_FORMAT), targetHost, targetPort)));
                        })
                .runAndWait();
    }

    private static FeatureCalculationsAlgorithm getExtraFeatures() {
        Map<String, FeatureCalculationsAlgorithm.FeatureCalculation> calculations = new HashMap<>();
        calculations.put("extra/cpu-per-net", access -> calculateDiff(access.getFeature("net-io/bytes"), access.getFeature("cpu")));
        calculations.put("extra/cpu-per-disk", access -> calculateDiff(getDisk(access), access.getFeature("cpu")));
        calculations.put("extra/net-packet-size", access -> calculateDiff(access.getFeature("net-io/packets"), access.getFeature("net-io/bytes")));
        return new FeatureCalculationsAlgorithm(calculations);
    }

    private static double getDisk(FeatureCalculationsAlgorithm.FeatureAccess access) {
        double disk;
        try {
            disk = access.getFeature("disk-io/vda/ioTime");
        } catch(IllegalArgumentException e) {
            disk = access.getFeature("disk-io/sda/ioTime");
        }
        return disk;
    }

    private static double calculateDiff(double val1, double val2) {
        // val1 and val2 already are in range 0..1 (mostly)
        // Try to also place the result in 0..1
        // Due to the value range, we can use subtraction instead of division
        return (val2 - val1) / 2 + 0.5;
    }

    private static class HostnameTagger extends AbstractAlgorithm {

        private final String hostname;
        public int samples_since_concept_change = 0;

        public HostnameTagger(String hostname) {
            this.hostname = hostname;
        }

        protected Sample executeSample(Sample sample) throws IOException {
            Sample output = new Sample(sample);
            output.setTag(SampleAnalysisOutput.TAG_HOSTNAME, hostname);
            output.setTag("sscc", String.valueOf(samples_since_concept_change));
            samples_since_concept_change++;
            return output;
        }

        @Override
        public String toString() {
            return "hostname tagger";
        }
    }

}
