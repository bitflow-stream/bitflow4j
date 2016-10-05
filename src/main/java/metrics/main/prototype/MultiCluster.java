package metrics.main.prototype;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.Algorithm;
import metrics.algorithms.FeatureCalculationsAlgorithm;
import metrics.algorithms.clustering.ClusterLabelingAlgorithm;
import metrics.algorithms.clustering.LabelAggregatorAlgorithm;
import metrics.algorithms.clustering.clustering.BICOClusterer;
import metrics.algorithms.evaluation.OnlineOutlierEvaluator;
import metrics.algorithms.normalization.OnlineAutoMinMaxScaler;
import metrics.io.fork.MultiFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.analysis.OpenStackSampleSplitter;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 6/21/16.
 */
public class MultiCluster {

    private static final double classifiedClusterThreshold = 0.1;
    private static final long labelAggregationWindow = 4000; // Milliseconds, must be declared as long

    public static void main(String[] args) throws IOException {
        if (args.length != 9) {
            System.err.println("Parameters: <receive-port> <feature ini file> <target-host> <FIRST target-port> <local hostname> <filter>"+
                    " <num_clusters> <concept-change-enabled> <incorrect-predictions-log>");
            return;
        }
        int receivePort = Integer.parseInt(args[0]);
        String statsFile = args[1];
        FeatureStatistics stats = new FeatureStatistics(statsFile);
        String targetHost = args[2];
        int startTargetPort = Integer.parseInt(args[3]);
        String hostname = args[4];
        String filter = args[5];
        int num_clusters = Integer.valueOf(args[6]);
        int num_micro_clusters = 500;
        boolean conceptChangeEnabled = Boolean.valueOf(args[7]);
        String incorrectPredictionsLog = args[8];
        Algorithm filterAlgo = Train.getFilter(filter);
        int restPort = 9000;

        int delayHours[] = new int[] { 5, 4, 3, 2, 1, 0 };

        List<Integer> targetPorts = new ArrayList<>();
        for (int i = 0; i < delayHours.length; i++) {
            targetPorts.add(startTargetPort + i);
        }

        Set<String> trainedLabels = new HashSet<>(Arrays.asList(new String[] { "idle", "load", "overload" }));

        new AlgorithmPipeline(receivePort, Analyse.TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            ClusterLabelingAlgorithm labeling = new ClusterLabelingAlgorithm(classifiedClusterThreshold, true).trainedLabels(trainedLabels);
                            LabelAggregatorAlgorithm labelAggregatorAlgorithm = new LabelAggregatorAlgorithm(labelAggregationWindow);
                            OnlineOutlierEvaluator evaluator = new OnlineOutlierEvaluator(true, trainedLabels, "normal", "abnormal");
                            evaluator.logIncorrectPredictions(incorrectPredictionsLog, hostname);
                            HostnameTagger hostnameTagger = new HostnameTagger(hostname);

                            BICOClusterer moaClusterer = new BICOClusterer(true, num_micro_clusters, num_clusters, null).trainedLabels(trainedLabels).alwaysAddDistanceMetrics();

                            // TODO switch to regular clusters
                            moaClusterer.useMicroclusters();
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

                            p
                                    .step(filterAlgo)
                                    .step(new OnlineAutoMinMaxScaler(0.5, conceptChangeHandler, stats))
                                    .step(extraFeatures)
                                    .fork(new MultiFork(targetPorts.size()),
                                            (num, sub) -> {
                                                int delay = delayHours[num];
                                                int targetPort = targetPorts.get(num);
                                                System.err.println("Configuring sub-pipeline nr " + num + ": delay " + delay + ", target port " + targetPort);
                                                sub
                                                    .step(new DelayAlgorithm(delay * 60 * 60))
                                                    .step(moaClusterer)
                                                    .step(labeling)
                                                    .step(labelAggregatorAlgorithm)
                                                    .step(evaluator)
                                                    .step(hostnameTagger)
                                                    .output(new TcpMetricsOutput(AlgorithmPipeline.getMarshaller(Analyse.TCP_OUTPUT_FORMAT), targetHost, targetPort));
                                            });
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

    }

    private static class DelayAlgorithm extends AbstractAlgorithm {

        private final Date start;
        private int delaySeconds;
        boolean started = false;

        public DelayAlgorithm(int delaySeconds) {
            this.delaySeconds = delaySeconds;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, delaySeconds);
            start = cal.getTime();
        }

        @Override
        protected Sample executeSample(Sample sample) throws IOException {
            if (new Date().after(start)) {
                if (!started) {
                    System.err.println("STARTING DELAY ALGORITHM " + delaySeconds);
                    started = true;
                }
                return sample;
            } else {
                return null;
            }
        }

    }

}
