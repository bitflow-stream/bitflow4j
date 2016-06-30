package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SampleWindow;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author fschmidt
 */
public abstract class AbstractMOAClusterer extends WindowBatchAlgorithm {
    final SampleWindow window = new SampleWindow();

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    Instances createDataset() {
        if (window.numSamples() < 1)
            throw new IllegalStateException("Cannot create empty dataset");
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), window.numSamples());
        for (String field : window.getHeader().header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allClasses());
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    private ArrayList<String> allClasses() {
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        for (Sample sample : window.samples) {
            allLabels.add(sample.getLabel());
        }
        return new ArrayList<>(allLabels);
    }

    synchronized void fillDataset(Instances instances) {
        for (Sample sample : window.samples) {
            double[] values = sample.getMetrics();
            values = Arrays.copyOf(values, values.length + 1);
            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(instances);
            instance.setClassValue(sample.getLabel());
            instances.add(instance);
        }
    }
}
