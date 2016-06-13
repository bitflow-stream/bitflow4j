package metrics.algorithms.clustering;

import moa.clusterers.AbstractClusterer;
import moa.clusterers.clustream.Clustream;

/**
 *
 * @author fschmidt
 */
public enum ExternalClusterer {
    CLUSTREAM(Clustream::new);

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
