package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.classification.AbstractWekaAlgorithm;
import metrics.algorithms.classification.Model;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.io.MetricOutputStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class can be used to evaluate any classification algorithm stack.
 * In order to use this evaluator, the samples must be tagged with the correct predictions
 * (e.g. by using the {@link ExpectedPredictionTagger}.
 * This is a obsolete algorithm and not suited for online streaming (can be added by allowing
 * variable window size in {@link AbstractWekaAlgorithm}
 */
public class WekaEvaluationWrapper extends AbstractWekaAlgorithm {

    protected Model model;
    protected Evaluation eval;
    protected Classifier classifier;

    public WekaEvaluationWrapper(){
        this.classifier = new WekaClassifierWrapper();

    }

    /**
     * Trains the underlying {@link WekaClassifierWrapper} with the predictions and evaluates the classification
     * result using the
     * @param output an output stream for the metrics TODO: what is this?
     * @throws IOException if an error occurs
     */
    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        //TODO: im not sure wether we really need the newly predicted instances trained to the classifier wrapper
        // or if the evaluator will retrain anyways (would not work with current implementation)
        String result = "";
        double []resultMetrics;
        Instances predictedInstances = this.createDataset();
        Instances testInstances = this.createTestDataset();
        this.fillDataset(predictedInstances);
        this.fillTestDataset(testInstances);
        //why do we need to build this classifier ?
        try {
            classifier.buildClassifier(predictedInstances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.eval = new Evaluation(predictedInstances);
        } catch (Exception e) {
            //exception is never thrown in reallity (checked source), might cause npe if instances is null or malformed
            e.printStackTrace();
        }
        try {
            //TODO: will the evaluator do the call?
//            classifier.buildClassifier(predictedInstances);
        } catch (Exception e) {
            //happens only if instances is empty
            e.printStackTrace();
        }
        try {
             resultMetrics = eval.evaluateModel(classifier, testInstances);
            //this is as far as the original evaluator went....
            //TODO: from here on we need to find out what we can evaluate and what to do with it
        } catch (Exception e) {
            //TODO: find out what happens here
            e.printStackTrace();
        }
        System.out.println(eval.toSummaryString(true));
        //TODO maybe merge try catch

    }

    private void fillTestDataset(Instances testInstances) throws IOException {
        CITInstance.resetCounter();
        for (Sample sample : window.samples) {
            double[] values = sample.getMetrics();
            values = Arrays.copyOf(values, values.length + 1);
            Instance instance = new CITInstance(1.0, values);
            instance.setDataset(testInstances);
            String expectedPrediction = sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG);
            if(expectedPrediction == null) throw new IOException("Sample not prepared for evaluation (no expected pridiction tag present)");
            System.out.println("predicted label to set: " + expectedPrediction);
            System.out.println("orig label: " + sample.getLabel());
            instance.setClassValue(expectedPrediction);
            testInstances.add(instance);
        }
    }

    private Instances createTestDataset() {
        if (window.numSamples() < 1)
            throw new IllegalStateException("Cannot create empty dataset");
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), window.numSamples());
        for (String field : window.getHeader().header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allTestClasses());
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    @Override
    public String toString() {
        return "weka-evaluation-wrapper";
    }
    //TODO: all the error handling etc
    public ArrayList<String> allTestClasses() {
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        for (Sample sample : window.samples) {
            allLabels.add(sample.getTag(ClusterConstants.EXPECTED_PREDICTION_TAG));
        }
        return new ArrayList<>(allLabels);
    }
}
