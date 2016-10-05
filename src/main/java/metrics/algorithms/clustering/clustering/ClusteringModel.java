package metrics.algorithms.clustering.clustering;

import metrics.algorithms.Model;

/**
 * Created by Malcolm-X on 30.09.2016.
 */
public interface ClusteringModel<T> extends Model<T> {

    /**
     * Returns the Clustering for the underlying model.
     *
     * @return the clustering or null if no Clustering is available
     */
    Clustering getClustering();
}
