package metrics.algorithms.classification;

import metrics.algorithms.logback.NoNanMetricLog;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.algorithms.logback.SampleMetadata;
import metrics.io.MetricOutputStream;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.*;

/**
 * @author fschmidt
 */
public class DecisionTreeClassificationAlgorithm extends PostAnalysisAlgorithm<NoNanMetricLog> {

    private J48 cls;
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
        allLabels.remove("cooldown");

        Instances trainingSet = new Instances("train", new ArrayList<>(), samples.size() + 1);
        for (String field : constructHeader(0).header) {
            trainingSet.insertAttributeAt(new Attribute(field), trainingSet.numAttributes());
        }
        Attribute attr = new Attribute("label", new ArrayList<>(allLabels));
        trainingSet.insertAttributeAt(attr, trainingSet.numAttributes());
        trainingSet.setClass(trainingSet.attribute(trainingSet.numAttributes() - 1));

        Instances testSet = new Instances("test", new ArrayList<>(), samples.size() + 1);
        for (String field : constructHeader(0).header) {
            testSet.insertAttributeAt(new Attribute(field), testSet.numAttributes());
        }
        testSet.insertAttributeAt(attr, testSet.numAttributes());
        testSet.setClass(testSet.attribute(testSet.numAttributes() - 1));

        Random rnd = new Random();
        double trainingSetCount = 0.8;

        int sampleCount = 0;
        for (SampleMetadata sample : samples) {
            if (sample.label.equals("cooldown")) continue;

            double randDouble = rnd.nextDouble();
            Instances targetSet = randDouble < trainingSetCount ? trainingSet : testSet;

            double[] values = this.getSampleValues(sampleCount);
            values = Arrays.copyOf(values, values.length + 1);
            Instance instance = new DenseInstance(1.0, values);
            instance.setDataset(targetSet);
            instance.setClassValue(sample.label);
            targetSet.add(instance);
            sampleCount++;
        }

        // Train Model
        cls = new J48();
        try {
            cls.buildClassifier(trainingSet);
        } catch (Exception ex) {
            throw new IOException("Training failed", ex);
        }

        // Validate model
        try {
            eval = new Evaluation(trainingSet);
            eval.evaluateModel(cls, testSet);
        } catch (Exception ex) {
            throw new IOException("Evaluation failed", ex);
        }

        // TODO
        System.out.println(resultsString());
        output.close();
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

    public String getGraphString() throws IOException {
        try {
            return cls.graph();
        } catch (Exception e) {
            throw new IOException("Failed to create graph dot-string", e);
        }
    }

}
