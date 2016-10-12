package metrics.main.prototype;

import metrics.algorithms.classification.WekaLearner;
import metrics.main.Config;
import metrics.main.TrainedDataModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by anton on 6/23/16.
 */
public class DrawTree {

    static {
        Config.initializeLogger();
    }

    private static final Logger logger = Logger.getLogger(DrawTree.class.getName());

    static final boolean printHeaderFields = false;

    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            logger.severe("Parameters: <model file> <output file>");
            return;
        }
        String modelFile = args[0];
        String output = args[1];
        TrainedDataModel model = Analyse.getDataModel(modelFile);

        model.allClasses.sort(String.CASE_INSENSITIVE_ORDER);
        logger.info("All classes: " + model.allClasses);
        logger.info("Header fields num " + model.headerFields.length);
        if (printHeaderFields)
            logger.info(Arrays.toString(model.headerFields));
        logger.info("Num avgs: " + model.averages.size() + ", num stddevs: " + model.stddevs.size());
        if (model instanceof TrainedDataModel2) {
            TrainedDataModel2 model2 = (TrainedDataModel2) model;
            logger.info("Num mins: " + model2.mins.size() + ", num maxs: " + model2.maxs.size());
        } else
            logger.warning("No min/max info stored.");

        WekaLearner.createPng(new File(output), model.model);
    }

}
