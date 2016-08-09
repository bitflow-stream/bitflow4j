package metrics.algorithms.clustering;

import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.SampleConverger;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author fschmidt, mbyfield
 */
public abstract class MOAStreamClusterer<T extends AbstractClusterer & Serializable> extends AbstractAlgorithm {

    protected final T clusterer;
    protected final SampleConverger converger = new SampleConverger(); // No predefined expected header
    protected final boolean alwaysTrain;
    protected final Set<String> trainedLabels;

    protected boolean calculateDistance;
//    protected Clustering clusteringResult;

    protected Clustering clusteringResult;

    // false -> train on all VALID labels (not empty)
    public MOAStreamClusterer(T clusterer, boolean alwaysTrain, boolean calculateDistance) {
        this.clusterer = clusterer;
        this.alwaysTrain = alwaysTrain;
        this.trainedLabels = alwaysTrain ? null : new HashSet<>(Arrays.asList(new String[]{"idle"}));
//        bico_num_clusters = 500;
        this.calculateDistance = calculateDistance;
//        clusteringResult = this.getClusteringResult();
    }

    // Train cluster only for certain labels (or give null-set to train on all VALID labels)
    public MOAStreamClusterer(T clusterer, Set<String> trainedLabels, boolean calculateDistance) {
        this.clusterer = clusterer;
        this.alwaysTrain = false;
        this.trainedLabels = trainedLabels;
        this.calculateDistance = calculateDistance;
//        clusteringResult = null;
    }

    protected void initalizeClusterer(Sample firstSample) {
        this.setupClustererParameter(firstSample);
        this.clusterer.resetLearning();
        this.printClustererParameters();
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        if (converger.getExpectedHeader() == null) {
            initalizeClusterer(sample);
        }
        String label = getLabel(sample);
        double values[] = converger.getValues(sample);

        Header expectedHeader = converger.getExpectedHeader();
        Instances instances = createInstances(expectedHeader, label);
        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(values, label, instances);

        boolean trained = false;
        boolean matchingClusterFound;
        if (alwaysTrain || (label != null && !label.isEmpty() && (trainedLabels == null || trainedLabels.contains(label)))) {
            clusterer.trainOnInstance(instance);
            trained = true;
            this.clusteringResult = this.getClusteringResult();
            matchingClusterFound = true;
        } else {
            matchingClusterFound = clusteringResult.getMaxInclusionProbability(instance) != 0;
        }

        Map.Entry<Double, double[]> distance = null;
        double inclusionProbability = 0.0;
        int bestFitCluster = -1;
        int clusterNum = 0;
        AutoExpandVector<Cluster> clustering = clusteringResult.getClustering();
        if (matchingClusterFound) {
            for (moa.cluster.Cluster c : clustering) {
                double clusterInclusionProbability = c.getInclusionProbability(instance);
                if (inclusionProbability < clusterInclusionProbability) {
                    inclusionProbability = clusterInclusionProbability;
                    bestFitCluster = clusterNum;
                }
                clusterNum++;
            }
        } else if (calculateDistance) {
            distance = getDistance(instance, clusteringResult);
        }

        sample.setTag(ClusterConstants.CLUSTER_TAG, Integer.toString(bestFitCluster));
        if (distance != null) {
            String newHeader[] = new String[distance.getValue().length + 1];
            double newValues[] = new double[newHeader.length];
            for (int i = 0; i < newHeader.length - 1; i++) {
                newHeader[i] = ClusterConstants.DISTANCE_PREFIX + sample.getHeader().header[i];
                newValues[i] = distance.getValue()[i];
            }
            newHeader[newHeader.length - 1] = ClusterConstants.DISTANCE_PREFIX + "overall";
            newValues[newValues.length - 1] = distance.getKey();
            sample = sample.extend(newHeader, newValues);
        }
        return sample;
    }

    public synchronized void resetClusters() {
        clusterer.resetLearning();
    }

    protected String getLabel(Sample sample) {
        String label = sample.getLabel();
        if (label == null || label.isEmpty()) {
            label = ClusterConstants.UNKNOWN_LABEL;
        }
        return label;
    }

    @Override
    public String toString() {
        return "moa clusterer";
    }

    protected com.yahoo.labs.samoa.instances.Instance makeInstance(double values[], String label, Instances instances) {
        values = Arrays.copyOf(values, values.length + 1);
        Instance instance = new DenseInstance(1.0, values);
        instance.setDataset(instances);
        instance.setClassValue(label);
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        return converter.samoaInstance(instance);
    }

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

    protected ArrayList<String> allClasses(String label) {
        // TODO is this necessary?
        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
        allLabels.add(label);
        return new ArrayList<>(allLabels);
    }

    protected abstract void printClustererParameters();

    protected abstract void setupClustererParameter(Sample firstSample);

    protected abstract Clustering getClusteringResult();

    protected abstract Map.Entry<Double, double[]> getDistance(com.yahoo.labs.samoa.instances.Instance instance, Clustering clustering) throws IOException;

}
