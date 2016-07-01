package metrics.algorithms.classification;

import metrics.Header;
import metrics.Sample;
import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by anton on 4/23/16.
 */
public class WekaClassifier<T extends Classifier & Serializable> extends AbstractWekaAlgorithm {

    private final Model<T> model;

    public WekaClassifier(Model<T> model) {
        this.model = model;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        Instances dataset = createDataset();
        Header header = window.getHeader();

        for (Sample sample : window.samples) {
            double[] values = sample.getMetrics();
            values = Arrays.copyOf(values, values.length + 1);

            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(dataset);
            try {
                double result = model.getModel().classifyInstance(instance);

                // TODO convert the result double back to the label string classAttribute().value(result)
                String label = instance.classAttribute().value((int) result);

                Sample classified = new Sample(header, sample.getMetrics(), sample);
                classified.setLabel(label);
                output.writeSample(classified);
            } catch (Exception e) {
                throw new IOException(toString() + "Classification failed", e);
            }
        }

    }

    @Override
    public String toString() {
        return "weka classifier";
    }

}
