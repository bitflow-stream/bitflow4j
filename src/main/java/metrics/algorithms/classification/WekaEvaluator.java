package metrics.algorithms.classification;

import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by anton on 4/23/16.
 */
public class WekaEvaluator<T extends Classifier & Serializable> extends AbstractWekaAlgorithm {

    private final Model<T> model;
    private Evaluation eval = null;

    public WekaEvaluator(Model<T> model) {
        super(true);
        this.model = model;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Instances testSet = createDataset();
        fillDataset(testSet);
        try {
            // TODO testSet in Evaluation constructor must actually be a training set...
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
        return eval.toSummaryString("\nResults\n======\n", false);
    }

    @Override
    public String toString() {
        return "weka evaluator";
    }

}
