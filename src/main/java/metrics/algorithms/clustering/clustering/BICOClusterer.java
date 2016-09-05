package metrics.algorithms.clustering.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.clusterers.kmeanspm.BICO;

import java.util.Set;

/**
 * @author mbyfield
 * Implementation of the BICO clustering algorithm.
 */
public class BICOClusterer extends MOASphereClusterer<BICO> {

    private volatile Integer numberOfProjections;
    private volatile Integer numberOfClusters;
    private volatile Integer numberOfCenters;

    public BICOClusterer setNumberOfProjections(Integer numberOfProjections) {
        this.numberOfProjections = numberOfProjections;
        return this;
    }

    public BICOClusterer setNumberOfClusters(Integer numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
        return this;
    }

    public BICOClusterer setNumberOfCenters(Integer numberOfCenters) {
        this.numberOfCenters = numberOfCenters;
        return this;
    }

    public BICOClusterer(boolean calculateDistance, Integer numberOfClusters, Integer numberOfCenters, Integer numberOfProjections) {
        super((BICO) ClusteringAlgorithm.BICO.newInstance(), calculateDistance);
        this.numberOfClusters = numberOfClusters;
        this.numberOfCenters = numberOfCenters;
        this.numberOfProjections = numberOfProjections;
    }

    public BICOClusterer alwaysAddDistanceMetrics() {
        super.alwaysAddDistanceMetrics();
        return this;
    }

    public BICOClusterer trainedLabels(Set<String> trainedLabels) {
        super.trainedLabels(trainedLabels);
        return this;
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
        this.clusterer.numClustersOption = numClustersOption;
        this.clusterer.maxNumClusterFeaturesOption = maxNumClusterFeaturesOption;
        this.clusterer.numDimensionsOption = numDimensionsOption;
        this.clusterer.numProjectionsOption = numProjectionsOption;
    }
}
