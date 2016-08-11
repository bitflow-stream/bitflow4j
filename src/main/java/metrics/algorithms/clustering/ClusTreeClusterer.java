package metrics.algorithms.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import moa.clusterers.clustree.ClusTree;

import java.util.Map;
import java.util.Set;
/**
 * @author mbyfield
 * Implementation of the Clustree clustering algorithm.
 */
public class ClusTreeClusterer extends MOASphereClusterer<ClusTree> {

    private final Integer horizon;
    private final Integer height;

    public ClusTreeClusterer(boolean alwaysTrain, boolean calculateDistance, Integer horizon, Integer height) {
        super((ClusTree) ExternalClusterer.CLUSTREE.newInstance(), alwaysTrain, calculateDistance);
        this.horizon = horizon;
        this.height = height;
    }

    public ClusTreeClusterer(Set<String> trainedLabels, Map<String, Object> parameters, boolean calculateDistance, Integer horizon, Integer height) throws IllegalArgumentException {
        super((ClusTree) ExternalClusterer.CLUSTREE.newInstance(), trainedLabels, calculateDistance);
        this.horizon = horizon;
        this.height = height;
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        int numMetrics = firstSample.getHeader().header.length;
        numMetrics++; // The class/label attribute is added
        IntOption horizonOption = null;
        horizonOption = new IntOption("horizon",
                'h', "Range of the window.", horizon == null ? 1000 : horizon);
        IntOption maxHeightOption = new IntOption(
                "maxHeight", 'H',
                "The maximal height of the tree", height == null ? 8 : height);

        this.clusterer.horizonOption = horizonOption;
        this.clusterer.maxHeightOption = maxHeightOption;
    }
}
