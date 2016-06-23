package metrics.main.prototype;

import metrics.algorithms.AbstractFeatureScaler;
import metrics.algorithms.FeatureAggregator;
import metrics.algorithms.FeatureStandardizer;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaLearner;
import metrics.io.file.FileMetricReader;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import weka.classifiers.trees.J48;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/9/16.
 */
public class Train {

    static final String TRAINING_INPUT_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Need 2 parameters: <input " + TRAINING_INPUT_FORMAT + " file> <output file>");
            return;
        }
        TrainedDataModel model = createDataModel(args[0]);
        storeDataModel(args[1], model);
    }

    static TrainedDataModel createDataModel(String inputFile) throws IOException {
        J48 j48 = new J48();

        j48.setConfidenceFactor(0.5f);
        j48.setReducedErrorPruning(true);

        FeatureStandardizer standardizer = new FeatureStandardizer();
//        FeatureMinMaxScaler standardizer = new FeatureMinMaxScaler();

        WekaLearner<J48> learner = new WekaLearner<>(new Model<>(), j48);

        new AlgorithmPipeline(AlgorithmPipeline.fileReader(inputFile, TRAINING_INPUT_FORMAT, FileMetricReader.FILE_NAME))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(standardizer)
                                    .step(new FeatureAggregator(10000L).addAvg().addSlope())
                                    .step(learner);
                        })
                .runAndWait();

        TrainedDataModel2 dataModel = new TrainedDataModel2();
        dataModel.model = j48;
        dataModel.averages = new HashMap<>();
        dataModel.stddevs = new HashMap<>();
        dataModel.mins = new HashMap<>();
        dataModel.maxs = new HashMap<>();
        dataModel.allClasses = learner.allFlushedClasses;
        dataModel.headerFields = learner.flushedHeader.header;
        for (Map.Entry<String, AbstractFeatureScaler.MetricScaler> entry : standardizer.getScalers().entrySet()) {
            String name = entry.getKey();
            AbstractFeatureScaler.MetricScaler scaler = entry.getValue();
            if (!(scaler instanceof AbstractFeatureScaler.AbstractMetricScaler)) {
                throw new IllegalStateException("MetricScaler was not FeatureStandardizer.AbstractMetricScaler, but " +
                        scaler.getClass().toString());
            }
            AbstractFeatureScaler.AbstractMetricScaler  metric = (AbstractFeatureScaler.AbstractMetricScaler) scaler;
            dataModel.averages.put(name, metric.average);
            dataModel.stddevs.put(name, metric.stdDeviation);
            dataModel.mins.put(name, metric.min);
            dataModel.maxs.put(name, metric.max);
        }
        return dataModel;
    }

    static void storeDataModel(String cacheFile, TrainedDataModel model) throws IOException {
        System.err.println("Storing model to " + cacheFile);
        FileOutputStream file_out = new FileOutputStream(cacheFile);
        ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
        obj_out.writeObject(model);
        obj_out.close();
    }

}
