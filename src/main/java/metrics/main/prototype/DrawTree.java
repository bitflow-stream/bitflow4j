package metrics.main.prototype;

import metrics.algorithms.classification.WekaLearner;
import metrics.main.TrainedDataModel;

import java.io.File;
import java.io.IOException;

/**
 * Created by anton on 6/23/16.
 */
public class DrawTree {

    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            System.err.println("Parameters: <model file> <output file>");
            return;
        }
        String modelFile = args[0];
        String output = args[1];
        TrainedDataModel model = Analyse.getDataModel(modelFile);
        WekaLearner.createPng(new File(output), model.model);
    }

}
