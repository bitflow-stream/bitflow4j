package metrics.main.prototype;

import metrics.algorithms.MetricFilterAlgorithm;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaLearner;
import metrics.io.file.FileMetricReader;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import weka.classifiers.trees.J48;

import java.io.IOException;

/**
 * Created by anton on 6/26/16.
 */
public class TrainAggregated {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Need 3 parameters: <input " + Train.TRAINING_INPUT_FORMAT + " file> <ini-file> <output file>");
            return;
        }
        String input = args[0];
        String iniFile = args[1];
        String target = args[2];

        FeatureStatistics stats = new FeatureStatistics(iniFile);
        TrainedDataModel model = createDataModel(input, stats);
        Train.storeDataModel(target, model);
    }

    static TrainedDataModel createDataModel(String inputFile, FeatureStatistics stats) throws IOException {
        J48 classifier = Train.createClassifier();
        WekaLearner<J48> learner = new WekaLearner<>(new Model<>(), classifier);

        new AlgorithmPipeline(AlgorithmPipeline.fileReader(inputFile, Train.TRAINING_INPUT_FORMAT, FileMetricReader.FILE_NAME))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(new MetricFilterAlgorithm(
                                            "disk-usage///free", "disk-usage///free_avg", "disk-usage///free_slope",
                                            "disk-usage///used", "disk-usage///used_avg", "disk-usage///used_slope",
                                            "num_procs", "num_procs_avg", "num_procs_slope",
                                            "proc/vnf/num", "proc/vnf/num_avg", "proc/vnf/num_slope",
                                            "proc/vnf/threads", "proc/vnf/threads_avg", "proc/vnf/threads_slope"))
                                    .step(learner);
                        })
                .runAndWait();

        TrainedDataModel2 dataModel = new TrainedDataModel2();
        dataModel.fillFromStatistics(stats);
        dataModel.model = classifier;
        dataModel.allClasses = learner.allFlushedClasses;
        dataModel.headerFields = learner.flushedHeader.header;
        return dataModel;
    }

}
