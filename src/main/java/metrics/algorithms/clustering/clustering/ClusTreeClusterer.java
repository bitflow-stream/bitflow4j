package metrics.algorithms.clustering.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.clusterers.clustree.ClusTree;
/**
 * @author mbyfield
 * Implementation of the Clustree clustering algorithm.
 */
public class ClusTreeClusterer extends MOASphereClusterer<ClusTree> {

    private Integer horizon = null;
    private Integer height = null;

    public ClusTreeClusterer(){
        super((ClusTree) ClusteringAlgorithm.CLUSTREE.newInstance());
    }

    public ClusTreeClusterer(boolean calculateDistance, Integer horizon, Integer height) {
        super((ClusTree) ClusteringAlgorithm.CLUSTREE.newInstance(), calculateDistance);
        this.horizon = horizon;
        this.height = height;
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        IntOption horizonOption = new IntOption("horizon",
                'h', "Range of the window.", horizon == null ? 1000 : horizon);
        IntOption maxHeightOption = new IntOption(
                "maxHeight", 'H',
                "The maximal height of the tree", height == null ? 8 : height);

        this.clusterer.horizonOption = horizonOption;
        this.clusterer.maxHeightOption = maxHeightOption;
    }

    public ClusTreeClusterer setHorizon(Integer horizon) {
        this.horizon = horizon;
        return this;
    }

    public ClusTreeClusterer setHeight(Integer height) {
        this.height = height;
        return this;
    }
}
