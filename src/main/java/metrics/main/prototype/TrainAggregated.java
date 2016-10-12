package metrics.main.prototype;

import metrics.algorithms.Algorithm;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaLearner;
import metrics.io.file.FileMetricReader;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import weka.classifiers.trees.J48;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by anton on 6/26/16.
 */
public class TrainAggregated {

    private static final Logger logger = Logger.getLogger(TrainAggregated.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            logger.severe("Parameters: <input " + Train.TRAINING_INPUT_FORMAT + " file> <ini-file> <output file> <filter>");
            return;
        }
        String input = args[0];
        String iniFile = args[1];
        String target = args[2];
        String filter = args[3];

        FeatureStatistics stats = new FeatureStatistics(iniFile);
        TrainedDataModel model = createDataModel(input, stats, filter);
        Train.storeDataModel(target, model);
    }

    static TrainedDataModel createDataModel(String inputFile, FeatureStatistics stats, String filter) throws IOException {
        J48 classifier = Train.createClassifier();
        WekaLearner<J48> learner = new WekaLearner<>(new Model<>(), classifier);
        Algorithm filterAlgo = Train.getFilter(filter);

        new AlgorithmPipeline(AlgorithmPipeline.fileReader(inputFile, Train.TRAINING_INPUT_FORMAT, FileMetricReader.FILE_NAME))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                logger.severe("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(filterAlgo)
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
