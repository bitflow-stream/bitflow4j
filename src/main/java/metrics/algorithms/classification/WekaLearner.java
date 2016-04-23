package metrics.algorithms.classification;

import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by anton on 4/23/16.
 */
public class WekaLearner<T extends Classifier & Serializable> extends AbstractWekaAlgorithm {

    private final Model<T> model;
    private final T classifier;

    public WekaLearner(Model<T> model, T classifier) {
        super(true);
        this.model = model;
        this.classifier = classifier;
    }

    @Override
    public String toString() {
        return "weka learner";
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Instances instances = createDataset();
        fillDataset(instances);
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            throw new IOException(toString() + ": Learning failed", e);
        }
        model.setModel(classifier);
    }

}
