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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 4/23/16.
 */
public abstract class AbstractWekaAlgorithm extends PostAnalysisAlgorithm<NoNanMetricLog> {

    public AbstractWekaAlgorithm(boolean globalAnalysis) {
        super(globalAnalysis);
    }

    Instances createDataset() {
        Set<String> allLabels = new HashSet<>(samples.size());
        for (SampleMetadata sample : samples) {
            allLabels.add(sample.label);
        }

        Instances instances = new Instances(toString() + "data", new ArrayList<>(), samples.size() + 1);
        for (String field : constructHeader(0).header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", new ArrayList<>(allLabels));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));

        return instances;
    }

    void fillDataset(Instances instances) {
        int sampleCount = 0;
        for (SampleMetadata sample : samples) {
            double[] values = this.getSampleValues(sampleCount++);
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
