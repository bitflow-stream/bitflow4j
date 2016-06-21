package metrics.algorithms.classification;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.SampleConverger;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by anton on 4/23/16.
 */
public class WekaOnlineClassifier<T extends Classifier & Serializable> extends AbstractAlgorithm {

    private final Model<T> model;
    private final Instances dataset;
    private final SampleConverger converger;

    public WekaOnlineClassifier(Model<T> model, String[] headerFields, ArrayList<String> allClasses) {
        this.model = model;
        this.dataset = createDataset(headerFields, allClasses);
        System.err.println("Expecting header length " + headerFields.length);

        Header expectedHeader = new Header(headerFields, true);
        converger = new SampleConverger(expectedHeader);
    }

    private Instances createDataset(String[] headerFields, ArrayList<String> allClasses) {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), 0);
        for (String field : headerFields) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        System.err.println("Classes in dataset: " + allClasses);
        Attribute attr = new Attribute("class", allClasses);
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        double values[] = converger.getValues(sample);
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(dataset);
        try {
            double result = model.getModel().classifyInstance(instance);
            String label = dataset.classAttribute().value((int) result);
            return new Sample(sample.getHeader(),
                    sample.getMetrics(), sample.getTimestamp(), sample.getSource(), label);
        } catch (Exception e) {
            throw new IOException(toString() + "Classification failed", e);
        }
    }

    @Override
    public String toString() {
        return "weka online classifier";
    }

}
