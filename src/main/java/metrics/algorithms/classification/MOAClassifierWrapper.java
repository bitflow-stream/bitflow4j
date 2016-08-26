package metrics.algorithms.classification;

import com.yahoo.labs.samoa.instances.*;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.Algorithm;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.algorithms.clustering.MOAStreamClusterer;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.net.ADNode;
import weka.classifiers.bayes.net.BIFReader;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.classifiers.bayes.net.search.local.K2;
import weka.core.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.IOException;
import java.util.*;

/**
 * This class can be used to map the classification results of previous algorithms to
 */
public class MOAClassifierWrapper extends AbstractClassifier  implements Algorithm {

    protected Map<String, List<Instances>> classifiedSamplesBatch;
    protected ParentSet[] m_ParentSets;
//    public Estimator[][] m_Distributions;
    protected Discretize m_DiscretizeFilter = null;
    int m_nNonDiscreteAttribute = -1;
    protected ReplaceMissingValues m_MissingValuesFilter = null;
    protected int m_NumClasses;
    public Instances m_Instances;
    private int m_NumInstances;
//    ADNode m_ADTree;
//    protected BIFReader m_otherBayesNet = null;
//    boolean m_bUseADTree = false;
//    SearchAlgorithm m_SearchAlgorithm = new K2();
//    BayesNetEstimator m_BayesNetEstimator = new SimpleEstimator();

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capabilities.Capability.MISSING_VALUES);
        result.enable(Capabilities.Capability.NOMINAL_CLASS);
//        result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
        result.setMinimumNumberInstances(0);
        return result;
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        this.classifiedSamplesBatch = new HashMap<>();
        this.getCapabilities().testWithFail(instances);
        instances = new Instances(instances);
        instances.deleteWithMissingClass();

        //TODO: double check if this should be done here
        instances = this.normalizeDataSet(instances);
        this.m_Instances = new Instances(instances);
        this.m_NumInstances = this.m_Instances.numInstances();
        this.m_NumClasses = instances.numClasses();
//        if(this.m_bUseADTree) {
//            this.m_ADTree = ADNode.makeADTree(instances);
//        }
        //TODO: dont know what happens here exactly, parentset is just a copy of instances
        this.initStructure();
        //usually initializes the search algorithm
//        this.buildStructure();
        //call to bayes net estimator
//        this.estimateCPTs();
        //TODO: dont know about this....
        this.m_Instances = new Instances(this.m_Instances, 0);
//        this.m_ADTree = null;
    }

    public void initStructure() throws Exception {
        int nAttribute = 0;

        int iAttribute;
        for(iAttribute = 1; iAttribute < this.m_Instances.numAttributes(); ++iAttribute) {
            if(nAttribute == this.m_Instances.classIndex()) {
                ++nAttribute;
            }
        }

        this.m_ParentSets = new ParentSet[this.m_Instances.numAttributes()];

        for(iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); ++iAttribute) {
            this.m_ParentSets[iAttribute] = new ParentSet(this.m_Instances.numAttributes());
        }

    }


    protected Instances normalizeDataSet(Instances instances) throws Exception {
        this.m_nNonDiscreteAttribute = -1;
        Enumeration enu = instances.enumerateAttributes();

        while(enu.hasMoreElements()) {
            Attribute attribute = (Attribute)enu.nextElement();
            if(attribute.type() != 1) {
                this.m_nNonDiscreteAttribute = attribute.index();
            }
        }

        if(this.m_nNonDiscreteAttribute > -1 && instances.attribute(this.m_nNonDiscreteAttribute).type() != 1) {
            this.m_DiscretizeFilter = new Discretize();
            this.m_DiscretizeFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, this.m_DiscretizeFilter);
        }
//TODO we can use this to fill missing values, but this in handled by the converger
        this.m_MissingValuesFilter = new ReplaceMissingValues();
        this.m_MissingValuesFilter.setInputFormat(instances);
        instances = Filter.useFilter(instances, this.m_MissingValuesFilter);
        return instances;
    }

    @Override
    public String toString() {
        return "Weka Classifier Wrapper";
    }

    //BEGIN: copy from abstract algorithm
    public boolean catchExceptions = false;
    private Exception startedStacktrace = null;
    private boolean started = false;

    public synchronized final void start(MetricInputStream input, MetricOutputStream output) {
        if (startedStacktrace != null) {
            throw new IllegalStateException("Algorithm was already started: " + toString(), startedStacktrace);
        }
        startedStacktrace = new Exception("This is the stack when first starting this algorithm");
        MOAClassifierWrapper.Runner thread = new MOAClassifierWrapper.Runner(input, output);
        thread.setDaemon(false);
        thread.setName("Algorithm Thread '" + toString() + "'");
        thread.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void execute(MetricInputStream input, MetricOutputStream output) throws IOException {
        while (true) {
            try {
                executeStep(input, output);
            } catch (InputStreamClosedException exc) {
                try {
                    inputClosed(output);
                } catch (IOException ioExc) {
                    throw new InputStreamClosedException(ioExc);
                }
                throw exc;
            } catch (IOException exc) {
                if (catchExceptions) {
                    System.err.println("IO Error executing " + toString());
                    exc.printStackTrace();
                } else {
                    throw exc;
                }
            }
        }
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        Sample sample = input.readSample();
        printStarted();
        Sample outputSample = executeSample(sample);
        if (outputSample != null)
            output.writeSample(outputSample);
    }

    protected Sample executeSample(Sample sample) throws IOException {
        //predicted Instances
        Instances originalInstances = createInstances(sample.getHeader(), sample.getLabel());
        Instances labeledInstances = createInstances(sample.getHeader(), sample.getLabel());
        com.yahoo.labs.samoa.instances.Instance originalInstance = makeInstance(sample.getMetrics(), sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG) , originalInstances);
        com.yahoo.labs.samoa.instances.Instance labeledInstance = makeInstance(sample.getMetrics(), sample.getLabel(), originalInstances);


//        this.classifiedSamplesBatch.put(originalInstance.classValue(), originalInstances);
        return sample;
    }

    protected com.yahoo.labs.samoa.instances.Instance makeInstance(double values[], String label, Instances instances) {
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
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
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        allLabels.add(label);
        return new ArrayList<>(allLabels);
    }

    protected void inputClosed(MetricOutputStream output) throws IOException {
        // Hook for subclasses
    }

    protected synchronized void printStarted() {
        if (!started) {
            System.err.println("Starting " + toString() + "...");
            started = true;
        }
    }

    private class Runner extends Thread {

        private final MetricInputStream input;
        private final MetricOutputStream output;

        Runner(MetricInputStream input, MetricOutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void run() {
            String name = MOAClassifierWrapper.this.toString();
            try {
                execute(input, output);
            } catch (InputStreamClosedException exc) {
                System.err.println("Input closed for algorithm " + name);
            } catch (Throwable exc) {
                System.err.println("Error in " + getName());
                exc.printStackTrace();
            } finally {
                System.err.println(name + " finished");
                try {
                    output.close();
                } catch (IOException e) {
                    System.err.println("Error closing output of algorithm " + name);
                    e.printStackTrace();
                }
            }
        }

    }



}
