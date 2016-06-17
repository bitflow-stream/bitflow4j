package metrics.algorithms.classification;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 4/23/16.
 */
public class WekaOnlineClassifier<T extends Classifier & Serializable> extends AbstractAlgorithm {

    private final Model<T> model;
    private final Instances dataset;
    private Header expectedHeader;

    private OnlineNormalEstimator filledUpValues = new OnlineNormalEstimator();

    public WekaOnlineClassifier(Model<T> model, String[] headerFields, ArrayList<String> allClasses) {
        this.model = model;
        this.dataset = createDataset(headerFields, allClasses);
        System.err.println("Expecting header length " + headerFields.length);
        this.expectedHeader = new Header(headerFields, true);
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
        double values[] = getValues(sample);
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

    private double[] getValues(Sample sample) {
        double[] values = sample.getMetrics();
        Header incomingHeader = sample.getHeader();
        if (expectedHeader.hasTags != incomingHeader.hasTags) {
            expectedHeader = new Header(expectedHeader.header, incomingHeader);
        }
        if (incomingHeader.hasChanged(expectedHeader)) {

            // === Different strategies can be chosen to converge the incoming feature vector to the one that has been trained.
            // values = optimisticConvergeValues(incomingHeader, values);
            values = mappedConvergeValues(incomingHeader, values);

            if (filledUpValues.numSamples() % 20 == 0) {
                System.err.println("Number of unavailable metrics: " + filledUpValues);
            }
        } else if (expectedHeader != incomingHeader) {
            // Make next hasChanged faster.
            expectedHeader = incomingHeader;
        }
        return values;
    }

    private double[] optimisticConvergeValues(Header incomingHeader, double[] incomingValues) {
        double result[] = new double[expectedHeader.header.length];
        int incoming = 0;
        int numFakeValues = 0;
        for (int i = 0; i < result.length; i++) {
            // Expect same order of header fields, but incoming can have some additional fields.
            while (incoming < incomingHeader.header.length && !incomingHeader.header[incoming].equals(expectedHeader.header[i])) {
                incoming++;
            }
            if (incoming < incomingHeader.header.length) {
                result[i] = incomingValues[incoming];
                incoming++;
            } else {
                result[i] = 0;
                numFakeValues++;
            }
        }
        filledUpValues.handle((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

    private double[] mappedConvergeValues(Header incomingHeader, double[] incomingValues) {
        Map<String, Double> incomingMap = new HashMap<>();
        for (int i = 0; i < incomingValues.length; i++) {
            incomingMap.put(incomingHeader.header[i], incomingValues[i]);
        }

        double result[] = new double[expectedHeader.header.length];
        int numFakeValues = 0;
        for (int i = 0; i < result.length; i++) {
            Double incoming = incomingMap.get(expectedHeader.header[i]);
            if (incoming == null) {
                result[i] = 0;
                numFakeValues++;
            } else {
                result[i] = incoming;
            }
        }
        filledUpValues.handle((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

    @Override
    public String toString() {
        return "weka online classifier";
    }

}
