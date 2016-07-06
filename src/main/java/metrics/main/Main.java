package metrics.main;

import metrics.algorithms.FeatureStandardizer;
import metrics.algorithms.MetricFilterAlgorithm;
import metrics.algorithms.TimestampSort;
import metrics.algorithms.classification.ExternalClassifier;
import metrics.algorithms.clustering.ClusterLabelingAlgorithm;
import metrics.algorithms.clustering.ExternalClusterer;
import metrics.algorithms.clustering.LabelAggregatorAlgorithm;
import metrics.algorithms.clustering.MOAStreamClusterer;
import metrics.algorithms.evaluation.MOAStreamEvaluator;
import metrics.io.file.FileGroup;
import metrics.io.file.FileMetricReader;
import metrics.io.fork.TwoWayFork;
import metrics.main.analysis.ClassifierFork;
import metrics.main.analysis.OpenStackSampleSplitter;
import metrics.main.analysis.SourceLabellingAlgorithm;
import metrics.main.data.*;
import moa.clusterers.AbstractClusterer;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class Main {

    private static final Host bono = new Host("bono.ims", "virtual");
    private static final Host wally131 = new Host("wally131", "physical");
    private static final Host wally147 = new Host("wally147", "physical");

    private static final MockDataSource mockData = new MockDataSource();
    private static final DataSource<Host> oldData = new OldDataSource("experiments-old", true, false, false);
    private static final DataSource<Host> newData = new NewDataSource("experiments-new-2", false);
    private static final DataSource<Integer> tcpData = new TcpDataSource(9999, "BIN", 1);

    public static void main(String[] args) throws Exception {
//        allClassifiers();

//        allClusterers();
//        prepareData(bono);
        Host source = bono;
        FileGroup outputs = new FileGroup(new File(newData.makeOutputDir(source), "analysis"));
//
//        /*
//        new AlgorithmPipeline(new File(outputs.getFile("tsne") + ".csv"), FileMetricReader.FILE_NAME)
//                .step(new FeatureStandardizer())
//                .output(new OutputMetricPlotter<>(new JMathPlotter(), 0, 1))
//                .runAndWait();
//        System.exit(0);
//        */
//
//        // new AlgorithmPipeline(newData, source)
//        new AlgorithmPipeline(new File(preparedDataFile(source)), FileMetricReader.FILE_NAME)
////                    .cache(new File(outputs.getFile("cache")))
//                .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
////                .step(new SampleFilterAlgorithm((sample) -> sample.getTimestamp().after(new Date(2016 - 1900, 4, 1))))
////                .step(new SourceLabellingAlgorithm())
//                .fork(new OpenStackSampleSplitter().fillInfo(),
//                        (name, p) -> {
//                            String file = outputs.getFile(name.isEmpty() ? "default" : name);
////                            p.consoleOutput("CSV");
////                            p.csvOutput(file + ".csv");
////                            p.cache(new File(outputs.getFile("cache")));
//
////                            p
////                                    .step(new PCAAlgorithm(0.99))
////                                    .output(new OutputMetricPlotter<>(new ScatterPlotter(), file, 0, 1));
//
////                            p.step(new FeatureAggregator(10000L).addAvg().addSlope());
////                            p.csvOutput(file + "-agg.csv");
////                            p.fork(
////                                    new TwoWayFork(0.8f),
////                                    new TimeBasedTwoWayFork(0.3f),
////                                    new SortedTimeBasedFork(0.3f, new Date(2016 - 1900, 4, 10, 9, 8, 40)),
////                                    new ClassifierFork<>(new J48(), file + ".png"));
//
//                            p
//                                    .step(new PCAAlgorithm(0.99))
//                                    .output(new OutputMetricPlotter<>(new ScatterPlotter(), file, 0, 1));

//                            p.step(new FeatureAggregator(10000L).addAvg().addSlope());
//                            p.csvOutput(file + "-agg.csv");
//                            p.fork(
//                                    new TwoWayFork(0.8f),
//                                    new TimeBasedTwoWayFork(0.3f),
//                                    new SortedTimeBasedFork(0.3f, new Date(2016 - 1900, 4, 10, 9, 8, 40)),
//                                    new ClassifierFork<>(new J48(), file + ".png"));
//                            p
//                                    .step(new FeatureStandardizer())
//                                    .step(new CobwebClusterer(0.7, false, 0, null))
//                                    .step(new PCAAlgorithm(0.99))
//                                    .output(new OutputMetricPlotter<>(new JMathPlotter(), 0, 1));
//                                    .step(new ClusterSummary(false, true))
//                                    .consoleOutput("TXT");
//                            p
//                                    .step(new TsneAlgorithm(5.0, 50, 2, false))
//                                    .csvOutput(outputs.getFile("tsne") + ".csv");
//                                    .consoleOutput();
//                        })
//                .runAndWait();
        new AlgorithmPipeline(new File(preparedDataFile(source)), FileMetricReader.FILE_NAME)
                .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                    String file = outputs.getFile(name.isEmpty() ? "default" : name);

                    p
                            .step(new FeatureStandardizer())
                            .step(new SourceLabellingAlgorithm())
                            .step(new MOAStreamClusterer<>(ExternalClusterer.BICO.newInstance(), true))
                            .step(new ClusterLabelingAlgorithm(0.0, false, false))
                            .step(new LabelAggregatorAlgorithm(10))
                            .step(new MOAStreamEvaluator(1000, true, false));

                })
                .runAndWait();
    }

    private static String preparedDataFile(Host source) throws IOException {
        return newData.makeOutputDir(source).toString() + "/sorted.csv";
    }

    private static void prepareData(Host source) throws IOException {
        // Combine, label and sort all available data for one host
        new AlgorithmPipeline(newData, source)
                .step(new SourceLabellingAlgorithm())
                .step(new TimestampSort(true))
                .csvOutput(preparedDataFile(source))
                .runAndWait();
        System.exit(0);
    }

    private static void allClassifiers() throws IOException {
        Host source = wally131;
//        String source = "mock-source";
        FileGroup outputs = new FileGroup(new File(newData.makeOutputDir(source), "analysis"));
        for (ExternalClassifier classifierEnum : ExternalClassifier.values()) {
            AbstractClassifier classifier = classifierEnum.newInstance();
            new AlgorithmPipeline(newData, source)
                    //                    .cache(new File(outputs.getFile("cache")))
                    .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                    .step(new SourceLabellingAlgorithm())
                    .fork(new OpenStackSampleSplitter().fillInfo(),
                            (name, p) -> {
                        String file = outputs.getFile(name.isEmpty() ? "default"
                                : name);
//                            p.consoleOutput("CSV");
//                            p.csvOutput(file + ".csv");
//                            Calendar c = Calendar.getInstance();
//                            c.set(2016, Calendar.MAY, 1);
//                            p.cache(new File(outputs.getFile("cache")));

                        p.fork(
                                new TwoWayFork(0.8f),
                                //                                new TimeBasedTwoWayFork(0.45f),
                                new ClassifierFork<>(classifier, file + ".png"));
                    })
                    .runAndWait();
        }
        System.exit(0);
    }

    private static void allClusterers() throws IOException {
        Host source = bono;
        FileGroup outputs = new FileGroup(new File(newData.makeOutputDir(source), "analysis"));

        for (ExternalClusterer clustererEnum : ExternalClusterer.values()) {
            AbstractClusterer clusterer = clustererEnum.newInstance();

            new AlgorithmPipeline(newData, source)
                    .cache(new File(outputs.getFile("cache")))
                    .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                    .step(new TimestampSort(false))
                    .step(new SourceLabellingAlgorithm())
                    .step(new FeatureStandardizer())
                    .fork(new OpenStackSampleSplitter().fillInfo(),
                            (name, p) -> {
                                p.step(new MOAStreamClusterer<>(clusterer, true));
                    })
                    .runAndWait();
        }
        System.exit(0);
    }

}
