package metrics.algorithms.clustering.clustering.moa;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import metrics.algorithms.clustering.clustering.*;
import moa.clusterers.AbstractClusterer;

/**
 * Created by Malcolm-X on 04.10.2016.
 */
public class MOAClusteringModel<T extends AbstractClusterer> implements ClusteringModel<T> {
    //TODO refactor and remove T
    private T clusterer;
    private boolean useMicroClusters = false;

    private Clustering clustering;

    public MOAClusteringModel(T clusterer, String[] header, boolean useMicroClusters) {
        this.useMicroClusters();
        this.clusterer = clusterer;
        this.clustering = new ClusteringImpl(header);
        setupClustering();
    }

    private void setupClustering() {
        moa.cluster.Clustering moaClustering = useMicroClusters ?
                clusterer.getMicroClusteringResult() : clusterer.getClusteringResult();
        for (moa.cluster.Cluster moaCluster : moaClustering.getClustering()) {
            //TODO prepare for other cluster kinds
            //TODO find solution for class keep or drop?
            if (moaCluster instanceof moa.cluster.SphereCluster) {
                moa.cluster.SphereCluster moaSphereCluster = (moa.cluster.SphereCluster) moaCluster;
                int id = (int) moaSphereCluster.getId();
                double[] center = moaSphereCluster.getCenter();
                double radius = moaSphereCluster.getRadius();
                int num = clusterer.getModelContext().numAttributes();
                String[] header = new String[num];
                InstancesHeader modelContext = clusterer.getModelContext();
                for (int i = 0; i < num; i++) {
                    header[i] = modelContext.attribute(i).name();
                }
                Cluster cluster = new SphereCluster(center, radius, header, id);
                this.clustering.add(cluster);
            }
        }
    }

    @Override
    public Clustering getClustering() {
        return this.clustering;
    }

    @Override
    public T getModel() {
        return clusterer;
    }

    public MOAClusteringModel useMicroClusters() {
        this.useMicroClusters = true;
        return this;
    }
}
