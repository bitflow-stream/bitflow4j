package metrics.algorithms.classification;

import metrics.algorithms.logback.NoNanMetricLog;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.algorithms.logback.SampleMetadata;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by anton on 4/23/16.
 */
public abstract class AbstractWekaAlgorithm extends PostAnalysisAlgorithm<NoNanMetricLog> {

    public AbstractWekaAlgorithm(boolean globalAnalysis) {
        super(globalAnalysis);
    }

    Instances createDataset() {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), samples.size());
        for (String field : constructHeader(0).header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allClasses());
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));

        return instances;
    }

    ArrayList<String> allClasses() {
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        for (SampleMetadata sample : samples) {
            allLabels.add(sample.label);
        }
        return new ArrayList<>(allLabels);
    }

    void fillDataset(Instances instances) {
        int sampleCount = 0;
        for (SampleMetadata sample : samples) {
            double[] values = getSampleValues(sampleCount++);
            values = Arrays.copyOf(values, values.length + 1);
            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(instances);
            instance.setClassValue(sample.label);
            instances.add(instance);
        }
    }

    @Override
    protected NoNanMetricLog createMetricStats(String name) {
        return new NoNanMetricLog(name);
    }

}
