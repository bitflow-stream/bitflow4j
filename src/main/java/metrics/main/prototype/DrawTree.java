package metrics.main.prototype;

import metrics.algorithms.classification.WekaLearner;
import metrics.main.TrainedDataModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by anton on 6/23/16.
 */
public class DrawTree {

    static final boolean printHeaderFields = false;

    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            System.err.println("Parameters: <model file> <output file>");
            return;
        }
        String modelFile = args[0];
        String output = args[1];
        TrainedDataModel model = Analyse.getDataModel(modelFile);

        model.allClasses.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("All classes: " + model.allClasses);
        System.out.println("Header fields num " + model.headerFields.length);
        if (printHeaderFields)
            System.out.println(Arrays.toString(model.headerFields));
        System.out.println("Num avgs: " + model.averages.size() + ", num stddevs: " + model.stddevs.size());
        if (model instanceof TrainedDataModel2) {
            TrainedDataModel2 model2 = (TrainedDataModel2) model;
            System.out.println("Num mins: " + model2.mins.size() + ", num maxs: " + model2.maxs.size());
        } else
            System.out.println("No min/max info stored.");

        WekaLearner.createPng(new File(output), model.model);
    }

}
