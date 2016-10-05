package metrics.main;

import metrics.CsvMarshaller;
import metrics.Sample;
import metrics.algorithms.*;
import metrics.algorithms.classification.ExternalClassifier;
import metrics.algorithms.classification.Model;
import metrics.algorithms.clustering.*;
import metrics.algorithms.clustering.clustering.moa.BICOClusterer;
import metrics.algorithms.clustering.obsolete.SimplePrinter;
import metrics.algorithms.evaluation.CrossValidationFork;
import metrics.algorithms.evaluation.ExpectedPredictionTagger;
import metrics.algorithms.evaluation.ExtendedStreamEvaluator;
import metrics.algorithms.filter.MetricFilterAlgorithm;
import metrics.algorithms.normalization.FeatureStandardizer;
import metrics.algorithms.rest.ExtendedRestServer;
import metrics.algorithms.rest.GraphWebServer;
import metrics.algorithms.rest.RestServer;
import metrics.io.MetricPrinter;
import metrics.io.file.FileGroup;
import metrics.io.file.FileMetricReader;
import metrics.io.fork.TwoWayFork;
import metrics.main.analysis.ClassifierFork;
import metrics.main.analysis.OpenStackSampleSplitter;
import metrics.main.analysis.SourceLabellingAlgorithm;
import metrics.main.data.*;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.Clusterer;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

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
        String jsonFile = "/home/malcolmx/Desktop/first_bico.json"; // change to point to correct file
        String outputFile = "/home/malcolmx/Desktop/out.csv"; //change to point correct file
        String inputFile = preparedDataFile(bono); // change to oint to correct file
//        allClassifiers();
//        allClusterers();
//        prepareData(bono);
//        bicoCVSplitPipeline(null);
//        printModelPipeline(jsonFile, outputFile, null).runAndWait();
//        testClusterReader(inputFile).runAndWait();
        graphTest(inputFile).runAndWait();

        Thread.sleep(1000000000L); //remove for normal close
    }

    private static AlgorithmPipeline graphTest(String inputFile) throws IOException {
        final String normalLabel = "normal";
        BICOClusterer bico = new BICOClusterer(false, 20, 10, null).trainedLabels(Collections.singleton(normalLabel));
        ClusterLabelingAlgorithm clusterLabeler = new ClusterLabelingAlgorithm(0.0, true);
        ExpectedPredictionTagger tagger = new ExpectedPredictionTagger();
        tagger.defaultLabel = ClusterConstants.NOISE_CLUSTER;
        tagger.addMapping(normalLabel, normalLabel);

        AbstractAlgorithm initialLabeller = new LabellingAlgorithm() {
            @Override
            protected String newLabel(Sample sample) {
                String label = sample.getSource();
                if (sample.hasLabel()) {
                    if (label.equals("idle") || label.equals("load") || label.equals("overload"))
                        return normalLabel;
                }
                return label;
            }
        };
        GraphWebServer server = new GraphWebServer(9000);
        server.addAlgorithm(bico, "bico");
        server.addAlgorithm(clusterLabeler, "cluster_labeler");
        server.start();
        AlgorithmPipeline pipe = new AlgorithmPipeline(new File(inputFile), FileMetricReader.FILE_NAME)
                .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                .step(tagger)
//                .step(new FeatureStandardizer())
                .step(initialLabeller)
                .step(bico)
                .step(clusterLabeler);
        return pipe;
    }

    private static AlgorithmPipeline testClusterReader(String inputFile) throws Exception {
        final String normalLabel = "normal";
        BICOClusterer bico = new BICOClusterer(false, 20, 10, null).trainedLabels(Collections.singleton(normalLabel));
        ClusterLabelingAlgorithm clusterLabeler = new ClusterLabelingAlgorithm(0.0, true);
        ExpectedPredictionTagger tagger = new ExpectedPredictionTagger();
        tagger.defaultLabel = ClusterConstants.NOISE_CLUSTER;
        tagger.addMapping(normalLabel, normalLabel);
        bico.alwaysTrain();
        AlgorithmPipeline pipeline = new AlgorithmPipeline(new File(inputFile), FileMetricReader.FILE_NAME);
        RestServer server = new ExtendedRestServer(9000);
        server.addAlgorithm(bico, "first_bico");
        server.start();
        pipeline.step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                .step(new SourceLabellingAlgorithm())
//                .step(new FeatureStandardizer())
                .step(tagger)
                .step(bico)
                .step(clusterLabeler)
                .runAndWait();
        return pipeline;
    }

    public static AlgorithmPipeline printModelPipeline(String jsonFile, String outputFile, ClusterReader cr) throws IOException {
        Host source = bono;
        //String jsonFile = "/home/malcolmx/Desktop/first_bico.json"; // change to point to correct file
        //String outputFile = "/home/malcolmx/Desktop/out.csv"; //change to point correct file
//        String jsonString = new Scanner(jsonFile).useDelimiter("\\Z").next();
        if (jsonFile != null && cr == null) {

            String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
            AbstractClusterer deserializedClusterer = MOAUtil.getClustererFromJSONString(jsonString);
            cr = new ClusterReader(deserializedClusterer);
        }
        CsvMarshaller marshaller = new CsvMarshaller();
        MetricPrinter printer = new MetricPrinter(outputFile, marshaller);
        AlgorithmPipeline pipeline = new AlgorithmPipeline(new File(preparedDataFile(source)), FileMetricReader.FILE_NAME);

        //TODO plot output
        cr.useMicroClusters();
        pipeline.step(cr);//.csvOutput(outputFile);
        pipeline.step(new SimplePrinter());
        return pipeline;
    }

    private static void bicoCVSplitPipeline(RestServer server) throws IOException, InterruptedException {
        if (server == null) {
            server = new RestServer(9000);
        }

        Host source = bono;
        FileGroup outputs = new FileGroup(new File(newData.makeOutputDir(source), "analysis"));

        final String normalLabel = "normal";
        Model<Clusterer> bicoModel = new Model<>();
        Model<ClusterCounter> labelingModel = new Model<>();
        BICOClusterer bico = new BICOClusterer(false, 500, 50, null).trainedLabels(Collections.singleton(normalLabel));
        BICOClusterer evalBico = new BICOClusterer(false, 500, 50, null).trainedLabels(Collections.singleton(normalLabel));
        ClusterLabelingAlgorithm clusterLabeler = new ClusterLabelingAlgorithm(0.0, true);
        ClusterLabelingAlgorithm evalClusterLabeler = new ClusterLabelingAlgorithm(0.0, true);
        ExpectedPredictionTagger tagger = new ExpectedPredictionTagger();
        tagger.defaultLabel = ClusterConstants.NOISE_CLUSTER;
        tagger.addMapping(normalLabel, normalLabel);

        AbstractAlgorithm initialLabeller = new LabellingAlgorithm() {
            @Override
            protected String newLabel(Sample sample) {
                String label = sample.getSource();
                if (sample.hasLabel()) {
                    if (label.equals("idle") || label.equals("load") || label.equals("overload"))
                        return normalLabel;
                }
                return label;
            }
        };
        server.addAlgorithm(bico, "first_bico");
        server.addAlgorithm(clusterLabeler, "first_cluster_labeler");
        server.addAlgorithm(evalClusterLabeler, "eval_cluster_labeler");
        server.addAlgorithm(evalBico, "eval_bico");
        server.start();
        // preparedDataFile(source)
        new AlgorithmPipeline(new File(preparedDataFile(source)), FileMetricReader.FILE_NAME)
                .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                    String file = outputs.getFile(name.isEmpty() ? "default" : name);
                    p
                            .step(initialLabeller)
                            .step(tagger)
                            .step(new FeatureStandardizer())
                            .fork(
                                    new CrossValidationFork(Collections.singleton("normal"), 0.8d),
                                    (key, pipeline) -> {
                                        pipeline.emptyOutput();
                                        switch (key) {
                                            case Primary:
                                                pipeline
                                                        .step(new AlgorithmModelProvider<>(bico, bicoModel))
                                                        .step(new AlgorithmModelProvider<>(clusterLabeler, labelingModel));
                                                break;
                                            case Secondary:
                                                pipeline
                                                        .step(new AlgorithmModelReceiver<>(evalBico, bicoModel))
                                                        .step(new AlgorithmModelReceiver<>(evalClusterLabeler, labelingModel))
//                                                        .step(new LabelAggregatorAlgorithm(10).stripData())
                                                        .step(new ExtendedStreamEvaluator(false));
                                                break;
                                        }
                                    });
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
                                new ClassifierFork<>(classifier, file + ".png"));
                    })
                    .runAndWait();
        }
        System.exit(0);
    }

    private static void allClusterers() throws IOException {
        Host source = bono;
        FileGroup outputs = new FileGroup(new File(newData.makeOutputDir(source), "analysis"));

        for (ClusteringAlgorithm clustererEnum : ClusteringAlgorithm.values()) {
            AbstractClusterer clusterer = clustererEnum.newInstance();

            new AlgorithmPipeline(newData, source)
                    .cache(new File(outputs.getFile("cache")))
                    .step(new MetricFilterAlgorithm("disk-usage///free", "disk-usage///used"))
                    .step(new TimestampSort(false))
                    .step(new SourceLabellingAlgorithm())
                    .step(new FeatureStandardizer())
                    .fork(new OpenStackSampleSplitter().fillInfo(),
                            (name, p) -> p.step(new BICOClusterer(true, null, null, null)))
                    .runAndWait();
        }
        System.exit(0);
    }

}
