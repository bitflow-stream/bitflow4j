package metrics.algorithms.classification;

import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 4/23/16.
 */
public class WekaEvaluator<T extends Classifier & Serializable> extends AbstractWekaAlgorithm {

    private final Model<T> model;
    private Evaluation eval = null;

    public WekaEvaluator(Model<T> model) {
        this.model = model;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        Instances testSet = createDataset();
        fillDataset(testSet);
        try {
            // TODO testSet in Evaluation constructor should be an extra training set?
            eval = new Evaluation(testSet);
            T modelObj = model.getModel();
            eval.evaluateModel(modelObj, testSet);
        } catch (Exception ex) {
            throw new IOException(toString() + ": Evaluation failed", ex);
        }
    }

    public String resultsString() {
        if (eval == null) {
            return toString() + ": not yet evaluated";
        }
        String result = eval.toSummaryString("\nResults\n======\n", false);
        try {
            result += eval.toMatrixString();
        } catch (Exception ex) {
            Logger.getLogger(WekaEvaluator.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void printResults(File file) {
        // TODO print to file
        System.out.println(resultsString());
    }

    @Override
    public String toString() {
        return "weka evaluator";
    }

}