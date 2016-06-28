package metrics.algorithms.clustering;

import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import moa.clusterers.clustream.Clustream;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class CluStreamClusterer extends AbstractAlgorithm {

    private Clustream cluster;
    private Instances instances = null;
    private Header incomingHeader = null;

    public CluStreamClusterer() {
        cluster = new Clustream();
        cluster.resetLearning();
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        if (incomingHeader == null) {
            initialize(sample);
        } else if (sample.headerChanged(incomingHeader)) {
            // TODO figure out how to update the attribute in the cluster
            throw new IllegalStateException("Changing header not supported");
        }

        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(sample);

        try {
            cluster.trainOnInstance(instance);
            double value = cluster.getMicroClusteringResult().getMaxInclusionProbability(instance);
            System.out.println(value);
//            double[] votes = cluster.getVotesForInstance(instance);
//            System.out.println(Arrays.toString(votes));
            int clusterNum = -1; // TODO get cluster id for instance
            Sample outSample = new Sample(sample.getHeader(), sample.getMetrics(), sample);
            outSample.setTag(ClusterConstants.CLUSTER_TAG, String.valueOf(clusterNum));
            return outSample;
        } catch (Exception e) {
            throw new IOException("Clustering failed", e);
        }
    }

    private void initialize(Sample sample) {
        incomingHeader = sample.getHeader();
        instances = createInstances(incomingHeader);
    }

    private Instances createInstances(Header header) {
        Instances instances = new Instances(toString() + " data", new ArrayList<>(), 0);
        
        List<String> labels = new ArrayList<>();
        labels.add("unknown"); //TODO: insert all different scenario labels
        labels.add("someA");
        Attribute attr = new Attribute("class", labels);
        instances.insertAttributeAt(attr, instances.numAttributes());
        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        
        for (String field : header.header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
        
        return instances;
    }

//    private ArrayList<String> allClasses() {
//        Set<String> allLabels = new TreeSet<>(); // Classes must be in deterministic order
//        for (Sample sample : window.samples) {
//            allLabels.add(sample.getLabel());
//        }
//        return new ArrayList<>(allLabels);
//    }
    private com.yahoo.labs.samoa.instances.Instance makeInstance(Sample sample) {
        Instance instance = new DenseInstance(1.0, sample.getMetrics());
        instance.setDataset(instances);
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        com.yahoo.labs.samoa.instances.Instance samoaInstance = converter.samoaInstance(instance);
        return samoaInstance;
    }

    @Override
    public String toString() {
        return "clustream moa";
    }

}
