package metrics.main.prototype;

import metrics.algorithms.Algorithm;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaOnlineClassifier;
import metrics.algorithms.normalization.FeatureAggregator;
import metrics.algorithms.normalization.OnlineFeatureMinMaxScaler;
import metrics.algorithms.normalization.OnlineFeatureStandardizer;
import metrics.io.MetricPrinter;
import metrics.io.fork.TwoWayFork;
import metrics.io.net.TcpMetricsOutput;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import weka.classifiers.trees.J48;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by anton on 6/9/16.
 */
public class Analyse {

    private static final Logger logger = Logger.getLogger(Analyse.class.getName());

    // This is also used in main-class Train
    public static final boolean USE_MIN_MAX_SCALING = false;

    static final String TCP_FORMAT = "BIN";
    static final String TCP_OUTPUT_FORMAT = "BIN";
    static final String CONSOLE_OUTPUT = "CSV";

    public static void main(String[] args) throws IOException {
        if (args.length != 6) {
            logger.severe("Parameters: <receive-port> <trained model file> <target-host> <target-port> <local hostname> <filter>");
            return;
        }
        int receivePort = Integer.parseInt(args[0]);
        TrainedDataModel model = getDataModel(args[1]);
        String targetHost = args[2];
        int targetPort = Integer.parseInt(args[3]);
        String hostname = args[4];
        String filter = args[5];
        Algorithm filterAlgo = Train.getFilter(filter);

        Model<J48> treeModel = new Model<>();
        treeModel.setModel(model.model);

        Algorithm standardizer;
        if (USE_MIN_MAX_SCALING) {
            standardizer = new OnlineFeatureMinMaxScaler(((TrainedDataModel2) model).mins, ((TrainedDataModel2) model).maxs);
        } else {
            standardizer = new OnlineFeatureStandardizer(model.averages, model.stddevs);
        }
        new AlgorithmPipeline(receivePort, TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                logger.severe("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }
                            p
                                    .step(filterAlgo)
                                    .step(standardizer)
                                    .step(new FeatureAggregator(10000L).addAvg().addSlope())
                                    .step(new WekaOnlineClassifier<>(treeModel, model.headerFields, model.allClasses))
                                    .step(new SampleAnalysisOutput(new HashSet<>(model.allClasses), hostname))
                                    .fork(new TwoWayFork(),
                                        (type, out) -> out.output(
                                                type == TwoWayFork.ForkType.Primary ?
                                                    new MetricPrinter(AlgorithmPipeline.getMarshaller(CONSOLE_OUTPUT)) :
                                                    new TcpMetricsOutput(AlgorithmPipeline.getMarshaller(TCP_OUTPUT_FORMAT), targetHost, targetPort)));
                        })
                .runAndWait();
    }

    static TrainedDataModel getDataModel(String inputFile) throws IOException {
        TrainedDataModel dataModel = loadDataModel(inputFile);
        if (dataModel == null) {
            throw new IOException("Failed to load trained model from " + inputFile);
        }
        return dataModel;
    }


    static TrainedDataModel loadDataModel(String cacheFile) throws IOException {
        if (!new File(cacheFile).exists()) return null;
        logger.info("Trying to load model from " + cacheFile);
        FileInputStream file_in = new FileInputStream(cacheFile);
        ObjectInputStream obj_in = new ObjectInputStream(file_in);
        Object obj;
        try {
            obj = obj_in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load DataModel", e);
        }
        if (obj instanceof TrainedDataModel) {
            return (TrainedDataModel) obj;
        } else {
            throw new IOException("Object in file " + cacheFile + " was " +
                    obj.getClass().toString() + " instead of DataModel");
        }
    }

}
