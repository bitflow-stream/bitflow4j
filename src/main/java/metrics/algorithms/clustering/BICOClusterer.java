package metrics.algorithms.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import moa.clusterers.kmeanspm.BICO;

import java.util.Set;
/**
 * @author mbyfield
 * Implementation of the BICO clustering algorithm.
 */
public class BICOClusterer extends MOASphereClusterer<BICO> {


    private final Integer numberOfProjections;
    private final Integer numberOfClusters;
    private final Integer numberOfCenters;

    public BICOClusterer(boolean alwaysTrain, boolean calculateDistance, Integer numberOfClusters, Integer numberOfCenters, Integer numberOfProjections) {
        super((BICO) ExternalClusterer.BICO.newInstance(), alwaysTrain, calculateDistance);
        this.numberOfClusters = numberOfClusters;
        this.numberOfCenters = numberOfCenters;
        this.numberOfProjections = numberOfProjections;
    }

    public BICOClusterer(Set<String> trainedLabels, boolean calculateDistance, Integer numberOfClusters, Integer numberOfCenters, Integer numberOfProjections) throws IllegalArgumentException {
        super((BICO) ExternalClusterer.BICO.newInstance(), trainedLabels, calculateDistance);
        this.numberOfClusters = numberOfClusters;
        this.numberOfCenters = numberOfCenters;
        this.numberOfProjections = numberOfProjections;
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        int numMetrics = firstSample.getHeader().header.length;
        numMetrics++; // The class/label attribute is added
        IntOption numClustersOption = new IntOption("Cluster", 'k',
                "Number of desired centers.", numberOfCenters == null ? 15 : numberOfCenters, 1, Integer.MAX_VALUE);
        IntOption numDimensionsOption = new IntOption("Dimensions", 'd',
                "Number of the dimensions of the input points.", numMetrics, 1,
                Integer.MAX_VALUE);
        IntOption maxNumClusterFeaturesOption = new IntOption(
                "MaxClusterFeatures", 'n', "Maximum size of the coreset.", numberOfClusters == null ? 500 : numberOfClusters, 1,
                Integer.MAX_VALUE);
        IntOption numProjectionsOption = new IntOption("Projections", 'p',
                "Number of random projections used for the nearest neighbour search.",
                numberOfProjections == null ? 1 : numberOfProjections, 1, Integer.MAX_VALUE);
//        this.bufferPhase = numberOfCenters+30;
        this.clusterer.numClustersOption = numClustersOption;
        this.clusterer.maxNumClusterFeaturesOption = maxNumClusterFeaturesOption;
        this.clusterer.numDimensionsOption = numDimensionsOption;
        this.clusterer.numProjectionsOption = numProjectionsOption;
    }
}
