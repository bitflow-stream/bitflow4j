package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.main.Config;
import weka.clusterers.Cobweb;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by anton on 5/13/16.
 */
public class CobwebClusterer extends AbstractAlgorithm {

    private Instances instances = null;
    private final Cobweb cluster;
    private final Map<Integer, Map<String, Integer>> clusterLabelMaps;
    private Header incomingHeader = null;
    private String outputPath;
    private boolean printGraphs = false;
    private int printClusterDetails = 0;
    private int sampleCount = 0;

    public CobwebClusterer(double acuity, boolean printGraphs, int printClusterDetails, String outputPath) {
        cluster = new Cobweb();
        cluster.setAcuity(acuity);
        //        cluster.setCutoff(1.0);
        //        cluster.setSeed(20);
        this.printGraphs = printGraphs;
        this.printClusterDetails = printClusterDetails;
        this.outputPath = outputPath;
        this.clusterLabelMaps = new HashMap<>();
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
        try {
            cluster.updateClusterer(instance);
            cluster.updateFinished();

            int i = cluster.clusterInstance(instance);
            if (clusterLabelMaps.containsKey(i)) {
                Map<String, Integer> labelCountMap = clusterLabelMaps.get(i);
                if (labelCountMap.containsKey(sample.getLabel())) {
                    labelCountMap.put(sample.getLabel(), labelCountMap.get(sample.getLabel()) + 1);
                } else {
                    labelCountMap.put(sample.getLabel(), 1);
                }
            } else {
                Map<String, Integer> labelCountMap = new HashMap<>();
                labelCountMap.put(sample.getLabel(), 1);
                clusterLabelMaps.put(i, labelCountMap);
            }

            if (printGraphs) {
                createPng(cluster.graph(), new File(outputPath + "/tree_" + (new Date()).getTime() + ".png"));
            }

            if (printClusterDetails > 0) {
                if (sampleCount % printClusterDetails == 0) {
                    System.out.println("##########MAP-COUNT##############");
                    List<Integer> clusterIds = new ArrayList<>(clusterLabelMaps.keySet());
                    Collections.sort(clusterIds);

                    for (Integer clusterId : clusterIds) {
                        System.out.println("----------------------");
                        System.out.println("cluster id: " + clusterId);
                        Map<String, Integer> scenarioCount = clusterLabelMaps.get(clusterId);
                        Stream<Map.Entry<String, Integer>> sorted = scenarioCount.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
                        sorted.forEach(System.out::println);
//                        for (String scenarioName : clusterLabelMaps.get(clusterId).keySet()) {
//
//                            System.out.println(scenarioName + " " + clusterLabelMaps.get(clusterId).get(scenarioName));
//                        }
                    }
                    System.out.println("##########END##############");
                    System.out.println("Sample: " + Arrays.toString(sample.getMetrics()));
                }
                sampleCount++;
            }
        } catch (Exception e) {
            throw new IOException("Cluster update failed", e);
        }

        try {
            int clusterNum = cluster.clusterInstance(instance);
            Sample outSample = new Sample(sample.getHeader(), sample.getMetrics(), sample);
            outSample.setTag(ClusterConstants.CLUSTER_TAG, String.valueOf(clusterNum));
            return outSample;
        } catch (Exception e) {
            throw new IOException("Clustering failed", e);
        }
    }

    public static void createPng(String dotString, File outputFile) throws
            IOException {
        String cmd[] = new String[]{Config.getInstance().getDotPath(), "-Tpng", "-o", outputFile.getAbsolutePath()};
        System.err.println("Executing command: " + Arrays.toString(cmd));
        Process dot = Runtime.getRuntime().exec(cmd);
        dot.getOutputStream().write(dotString.getBytes());
        dot.getOutputStream().close();
        try {
            dot.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
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
        return "weka cobweb";
    }

}
