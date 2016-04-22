package metrics.algorithms;

import metrics.algorithms.logback.NoNanMetricLog;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.algorithms.logback.SampleMetadata;
import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fschmidt
 */
public class DecisionTreeClassificationAlgorithm extends PostAnalysisAlgorithm<NoNanMetricLog> {

    private Classifier cls;
    private Evaluation eval;

    public DecisionTreeClassificationAlgorithm() {
        super(true);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        //Parse sample data to weka's Instances object

        Set<String> allLabels = new HashSet<>(samples.size());
        for (SampleMetadata sample : samples) {
            allLabels.add(sample.label);
        }

        Instances instances = new Instances("Rel", new ArrayList<>(), samples.size() + 1);
        for (String field : constructHeader(0).header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }

        Attribute attr = new Attribute("label", new ArrayList<>(allLabels));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));

        int sampleCount = 0;
        for (SampleMetadata sample : samples) {
            double[] values = this.getSampleValues(sampleCount);
            values = Arrays.copyOf(values, values.length + 1);
            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(instances);
            instance.setClassValue(sample.label);
            instances.add(instance);
            sampleCount++;
        }

        //Train Model
        cls = new J48();
        try {
            cls.buildClassifier(instances);
        } catch (Exception ex) {
            Logger.getLogger(DecisionTreeClassificationAlgorithm.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        //Validate model
        try {
            eval = new Evaluation(instances);
//            eval.evaluateModel(cls, test);
            eval.crossValidateModel(cls, instances, 10, new Random());

        } catch (Exception ex) {
            Logger
                    .getLogger(DecisionTreeClassificationAlgorithm.class
                            .getName())
                    .log(Level.SEVERE, null, ex);
        }

        //TODO: create outputStream

        System.out.println(resultsString());
    }

    public String resultsString() {
        return eval.toSummaryString("\nResults\n======\n", false);
    }

    @Override
    protected NoNanMetricLog createMetricStats(String name) {
        return new NoNanMetricLog(name);
    }

    @Override
    public String toString() {
        return "WEKA decision tree";
    }

}
