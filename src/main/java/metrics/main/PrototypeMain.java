package metrics.main;

import metrics.algorithms.AbstractFeatureScaler;
import metrics.algorithms.FeatureAggregator;
import metrics.algorithms.FeatureStandardizer;
import metrics.algorithms.OnlineFeatureStandardizer;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaLearner;
import metrics.algorithms.classification.WekaOnlineClassifier;
import metrics.io.file.FileMetricReader;
import metrics.main.analysis.OpenStackSampleSplitter;
import metrics.main.analysis.SampleClearer;
import weka.classifiers.trees.J48;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/9/16.
 */
public class PrototypeMain {

    static final int TCP_PORT = 8899;
    static final String TCP_FORMAT = "BIN";
    static final String TRAINING_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Need 1 parameter: input " + TRAINING_FORMAT + " file");
            return;
        }
        TrainedDataModel model = getDataModel(args[0]);
        Model<J48> treeModel = new Model<>();
        treeModel.setModel(model.model);
        new AlgorithmPipeline(TCP_PORT, TCP_FORMAT)
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(new FeatureAggregator(10000L).addAvg().addSlope())
                                    .step(new OnlineFeatureStandardizer(model.averages, model.stddevs))
                                    .step(new WekaOnlineClassifier<>(treeModel, model.headerFields, model.allClasses))
                                    .step(new SampleClearer())
                                    .consoleOutput("CSV");
                        })
                .runAndWait();
    }

    static TrainedDataModel getDataModel(String inputFile) throws IOException {
        String cacheFile = inputFile + ".model";
        TrainedDataModel dataModel = loadDataModel(cacheFile);
        if (dataModel != null) {
            System.err.println("Loaded cached model from " + cacheFile);
            return dataModel;
        }
        System.err.println("No cached model, computing model...");
        dataModel = createDataModel(inputFile);
        storeDataModel(cacheFile, dataModel);
        return dataModel;
    }

    static TrainedDataModel createDataModel(String inputFile) throws IOException {
        J48 j48 = new J48();
        FeatureStandardizer standardizer = new FeatureStandardizer();
        WekaLearner<J48> learner = new WekaLearner<>(new Model<>(), j48);

        new AlgorithmPipeline(AlgorithmPipeline.fileReader(inputFile, TRAINING_FORMAT, FileMetricReader.FILE_NAME))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(new FeatureAggregator(10000L).addAvg().addSlope())
                                    .step(standardizer)
                                    .step(learner);
                        })
                .runAndWait();

        TrainedDataModel dataModel = new TrainedDataModel();
        dataModel.model = j48;
        dataModel.averages = new HashMap<>();
        dataModel.stddevs = new HashMap<>();
        dataModel.allClasses = learner.allFlushedClasses;
        dataModel.headerFields = learner.flushedHeader.header;
        for (Map.Entry<String, AbstractFeatureScaler.MetricScaler> entry : standardizer.getScalers().entrySet()) {
            String name = entry.getKey();
            AbstractFeatureScaler.MetricScaler scaler = entry.getValue();
            if (!(scaler instanceof FeatureStandardizer.MetricStandardizer)) {
                throw new IllegalStateException("MetricScaler was not FeatureStandardizer.MetricStandardizer, but " +
                        scaler.getClass().toString());
            }
            FeatureStandardizer.MetricStandardizer metric = (FeatureStandardizer.MetricStandardizer) scaler;
            dataModel.averages.put(name, metric.average);
            dataModel.stddevs.put(name, metric.stdDeviation);
        }
        return dataModel;
    }

    static TrainedDataModel loadDataModel(String cacheFile) throws IOException {
        if (!new File(cacheFile).exists()) return null;
        System.err.println("Trying to load model from " + cacheFile);
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

    static void storeDataModel(String cacheFile, TrainedDataModel model) throws IOException {
        System.err.println("Storing model to " + cacheFile);
        FileOutputStream file_out = new FileOutputStream(cacheFile);
        ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
        obj_out.writeObject(model);
        obj_out.close();
    }

}
