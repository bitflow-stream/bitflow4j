package metrics.algorithms.clustering;

import moa.clusterers.AbstractClusterer;
import moa.clusterers.clustream.Clustream;
import moa.clusterers.clustree.ClusTree;
import moa.clusterers.denstream.WithDBSCAN;
import moa.clusterers.streamkm.StreamKM;

/**
 *
 * @author fschmidt
 */
public enum ExternalClusterer {
    CLUSTREAM(Clustream::new),
    CLUSTREE(ClusTree::new),
    DENSTREAM(WithDBSCAN::new),
    BICO(moa.clusterers.kmeanspm.BICO::new),
    STREAM_KMEANS(StreamKM::new);

    private final ClustererFactory clusterer;

    ExternalClusterer(ClustererFactory clusterer) {
        this.clusterer = clusterer;
    }

    public AbstractClusterer newInstance() {
        return clusterer.make();
    }

    private interface ClustererFactory {
        AbstractClusterer make();
    }
}
