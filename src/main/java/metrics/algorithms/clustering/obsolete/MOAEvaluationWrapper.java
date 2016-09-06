package metrics.algorithms.clustering.obsolete;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.MOAStreamClusterer;
import moa.core.InstanceExample;
import moa.evaluation.AdwinClassificationPerformanceEvaluator;
import moa.evaluation.BasicClassificationPerformanceEvaluator;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by malcolmx on 26.08.16.
 */
public abstract class MOAEvaluationWrapper extends AbstractAlgorithm{

    private BasicClassificationPerformanceEvaluator[] evaluators;

    public MOAEvaluationWrapper(){
        //default case
        defaultCase();
    }

    public MOAEvaluationWrapper(BasicClassificationPerformanceEvaluator... evaluators){
        if(evaluators != null && evaluators.length != 0)  this.evaluators = evaluators;
        else defaultCase();
        initializeEvaluators();
    }

    private void initializeEvaluators() {
        for (BasicClassificationPerformanceEvaluator evaluator : evaluators){
            evaluator.prepareForUse();
            evaluator.reset();
        }
    }

    public MOAEvaluationWrapper(MOAClassifierEvaluators... evaluators){
        //TODO: not supported yet
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String toString() {
        return "moa evaluation wrapper";
    }
    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
//        Arrays.stream(evaluators).parallel().forEach();
        //TODO a) do we need the converger b) should the converger be refactored to a super class (e.g. AbstractAlgorithm)?
        //TODO is this the correct label or should we use prediction here?
        Instances instances = createInstances(sample.getHeader(),sample.getLabel());
        Instance instance = makeInstance(sample.getMetrics(),sample.getLabel(),instances);
        for (BasicClassificationPerformanceEvaluator evaluator : evaluators){
            evaluator.addResult(new InstanceExample(instance), getClassVoteArray());
        }
        return super.executeSample(sample);
    }



    private void defaultCase() {
        this.evaluators = new BasicClassificationPerformanceEvaluator[]{new AdwinClassificationPerformanceEvaluator()};

    }

    //TODO refactor the following away either to static util class or to new super class abstractMoaAlgorithm (preferred)

    protected com.yahoo.labs.samoa.instances.Instance makeInstance(double values[], String label, Instances instances) {
        //TODO: refactor all of this stuff to a saperate class
        values = Arrays.copyOf(values, values.length + 1);
        weka.core.Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(instances);
        instance.setClassValue(label);
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        return converter.samoaInstance(instance);
    }

    /**
     * Creates an {@link Instances} object that can be used for the {@link MOAStreamClusterer#makeInstance(double[], String, Instances)} method.
     * @param header The header of the sample
     * @param label The label of the sample
     * @return An {@link Instances} object
     */
    protected Instances createInstances(Header header, String label) {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), 0);
        for (String field : header.header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        Attribute attr = new Attribute("class", allClasses(label));
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    /**
     * Adds the current label to the sorted set of all labels
     * @param label the current label
     * @return An @link{java.util.ArrayList} containing all labels
     */
    protected ArrayList<String> allClasses(String label) {
        // TODO is this necessary?
        //TODO: looks strange, Set is never saved, why copy it and why create a single valued set, might be a bug or obsolete artifact
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        allLabels.add(label);
        return new ArrayList<>(allLabels);
    }

    public double[] getClassVoteArray() {
        //TODO not implemented yet
        throw new UnsupportedOperationException("not implemented yet");
//        return null;
    }
}
