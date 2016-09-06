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
    private volatile Integer numberOfMicroclusters;
    private volatile Integer numberOfClusters;

    public BICOClusterer setNumberOfProjections(Integer numberOfProjections) {
        this.numberOfProjections = numberOfProjections;
        return this;
    }

    public BICOClusterer setNumberOfMicroclusters(Integer numberOfMicroclusters) {
        this.numberOfMicroclusters = numberOfMicroclusters;
        return this;
    }

    public BICOClusterer setNumberOfClusters(Integer numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
        return this;
    }

    public BICOClusterer(boolean calculateDistance, Integer numberOfMicroclusters, Integer numberOfClusters, Integer numberOfProjections) {
        super((BICO) ClusteringAlgorithm.BICO.newInstance(), calculateDistance);
        this.numberOfMicroclusters = numberOfMicroclusters;
        this.numberOfClusters = numberOfClusters;
        this.numberOfProjections = numberOfProjections;
    }

    public void copyClusterer(BICOClusterer source) {
        this.clusterer = source.clusterer;
        this.numberOfClusters = source.numberOfClusters;
        this.numberOfMicroclusters = source.numberOfMicroclusters;
        this.numberOfProjections = source.numberOfProjections;
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
                "Number of desired centers.", numberOfClusters == null ? 15 : numberOfClusters, 1, Integer.MAX_VALUE);
        IntOption numDimensionsOption = new IntOption("Dimensions", 'd',
                "Number of the dimensions of the input points.", numMetrics, 1,
                Integer.MAX_VALUE);
        IntOption maxNumClusterFeaturesOption = new IntOption(
                "MaxClusterFeatures", 'n', "Maximum size of the coreset.", numberOfMicroclusters == null ? 500 : numberOfMicroclusters, 1,
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
