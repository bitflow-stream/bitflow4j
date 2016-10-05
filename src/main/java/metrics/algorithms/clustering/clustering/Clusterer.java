package metrics.algorithms.clustering.clustering;

import metrics.algorithms.Algorithm;

/**
 * Created by Malcolm-X on 30.09.2016.
 */
public interface Clusterer extends Algorithm {

    @Override
    ClusteringModel getModel();
}
