package metrics.algorithms.clustering;

import com.yahoo.labs.samoa.instances.WekaToSamoaInstanceConverter;
import metrics.algorithms.classification.Model;
import metrics.io.MetricOutputStream;
import metrics.main.Config;
import metrics.main.misc.ParameterHash;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class MOABatchClusterer<T extends AbstractClusterer & Serializable> extends AbstractMOAClusterer {

    private final Model<T> model;
    private final T clusterer;
    private final Map<Integer, Map<String, Integer>> clusterLabelMaps;

    public MOABatchClusterer(Model<T> model, T clusterer) {
        this.model = model;
        this.clusterer = clusterer;
        this.clusterer.resetLearning();
        this.clusterLabelMaps = new HashMap<>();
    }

    @Override
    public String toString() {
        return "MOA clusterer";
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        Instances instances = createDataset();
        fillDataset(instances);
        try {
            for (Instance inst : instances) {
                com.yahoo.labs.samoa.instances.Instance instance = makeInstance(inst);

                clusterer.trainOnInstance(instance);
                AutoExpandVector<moa.cluster.Cluster> clustering = clusterer.getMicroClusteringResult().getClustering();
                System.out.println("########################");
//                for (moa.cluster.Cluster cluster : clustering) {
//                    System.out.println("ID: " + cluster.getInfo() + " , " + Arrays.toString(cluster.getCenter()));
//                }
                //prints all micro-clusters
                double inclusionProbability = 0.0;
                int bestFitCluster = -1;
                int clusterNum = 0;
                for (moa.cluster.Cluster c : clusterer.getMicroClusteringResult().getClustering()) {
//                    System.out.println(clusterNum + ": ");
//                    for (double p : c.getCenter()) {
//                        System.out.print(p + " ");
//                    }
//                    System.out.println(" " + clusterer.getMicroClusteringResult().dimension());
//                    System.out.println("Inclusion Probability: " + c.getInclusionProbability(instance));
                    double clusterInclusionProbability = c.getInclusionProbability(instance);
                    if(inclusionProbability <clusterInclusionProbability){
                        inclusionProbability = clusterInclusionProbability;
                        bestFitCluster = clusterNum;
                    }
                    clusterNum++;
                }

//                //Evaluate Clusters
//                if (clusterLabelMaps.containsKey(bestFitCluster)) {
//                    Map<String, Integer> labelCountMap = clusterLabelMaps.get(bestFitCluster);
//                    if (labelCountMap.containsKey(sample.getLabel())) {
//                        labelCountMap.put(sample.getLabel(), labelCountMap.get(sample.getLabel()) + 1);
//                    } else {
//                        labelCountMap.put(sample.getLabel(), 1);
//                    }
//                } else {
//                    Map<String, Integer> labelCountMap = new HashMap<>();
//                    labelCountMap.put(sample.getLabel(), 1);
//                    clusterLabelMaps.put(bestFitCluster, labelCountMap);
//                }
//                //Print Evaluation

            }
        } catch (Exception e) {
            IOException io = new IOException(toString() + ": Learning failed", e);
            model.modelProducerFailed(io);
            throw io;
        }
        model.setModel(clusterer);
        // TODO write Samples from window into output, set CLUSTER_TAG tag
    }

    public void printResults(File file) {
        if (file == null) {
            System.err.println("Not producing dot graph files.");
            return;
        }
        if (clusterer instanceof Drawable) {
            try {
                String graph = ((Drawable) clusterer).graph();
                createPng(graph, file);
            } catch (Exception e) {
                System.err.println("Failed to produce classification graph");
                e.printStackTrace();
            }
        } else {
            System.err.println("Not producing dot graph file: Instance of " + clusterer.getClass().toString() + " is not Drawable");
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

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeClassName(clusterer);
    }

    private com.yahoo.labs.samoa.instances.Instance makeInstance(Instance inst) {
        WekaToSamoaInstanceConverter converter = new WekaToSamoaInstanceConverter();
        com.yahoo.labs.samoa.instances.Instance samoaInstance = converter.samoaInstance(inst);
        return samoaInstance;
    }
}
