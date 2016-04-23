package metrics.algorithms.classification;

import metrics.Sample;
import metrics.algorithms.logback.SampleMetadata;
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
        super(true);
        this.model = model;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Instances dataset = createDataset();
        Sample.Header header = constructHeader(Sample.Header.TOTAL_SPECIAL_FIELDS);

        int sampleCount = 0;
        for (SampleMetadata meta : samples) {
            double[] values = this.getSampleValues(sampleCount);
            values = Arrays.copyOf(values, values.length + 1);

            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(dataset);
            try {
                double result = model.getModel().classifyInstance(instance);

                // TODO convert the result double back to the label string
                String label = String.valueOf(result);

                Sample sample = new Sample(header, getSampleValues(sampleCount), meta.timestamp, meta.source, label);
                output.writeSample(sample);
            } catch (Exception e) {
                throw new IOException(toString() + "Classification failed", e);
            }
            sampleCount++;
        }

    }

    @Override
    public String toString() {
        return "weka classifier";
    }

}
