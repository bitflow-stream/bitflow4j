/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics.algorithms.clustering;

import java.io.IOException;
import java.util.ArrayList;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import weka.clusterers.OPTICS;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author fschmidt
 */
public class OpticsClusterer extends AbstractAlgorithm {

    private final OPTICS cluster;
    private Instances instances = null;
    private Header incomingHeader = null;

    public OpticsClusterer() {
        cluster = new OPTICS();
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        if (incomingHeader == null) {
            initialize(sample);
        } else if (sample.headerChanged(incomingHeader)) {
            // TODO figure out how to update the attribute in the cluster
            throw new IllegalStateException("Changing header not supported");
        }

        Instance instance = makeInstance(sample);
//        cluster.updateClusterer(instance);
//        cluster.updateFinished();

        try {
            int clusterNum = cluster.clusterInstance(instance);
            String label = "Cluster-" + clusterNum;
            return new Sample(sample.getHeader(), sample.getMetrics(),
                    sample.getTimestamp(), sample.getSource(), label);
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
        for (String field : header.header) {
            instances.insertAttributeAt(new Attribute(field), instances.numAttributes());
        }
//        Attribute attr = new Attribute("class", allClasses());
//        instances.insertAttributeAt(attr, instances.numAttributes());
//        instances.setClass(instances.attribute(instances.numAttributes() - 1));
        return instances;
    }

    private Instance makeInstance(Sample sample) {
        Instance instance = new DenseInstance(1.0, sample.getMetrics());
        instance.setDataset(instances);
        return instance;
    }

    @Override
    public String toString() {
        return "weka optics";
    }

}
