package metrics.algorithms.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import moa.cluster.Clustering;
import moa.clusterers.streamkm.StreamKM;

import java.util.Map;
import java.util.Set;
/**
 * @author mbyfield
 * Implementation of the StreamKM clustering algorithm.
 */
public class StreamKMClusterer extends MOASphereClusterer<StreamKM> {
    private Integer sizeCoreset = null;
    private Integer numberOfClusters = null;
    private Integer width = null;
    private Integer randomSeed = null;

    //TODO sanetize parameters
    public StreamKMClusterer setSizeCoreset(Integer sizeCoreset) {
        this.sizeCoreset = sizeCoreset;
        return this;
    }

    public StreamKMClusterer setNumberOfClusters(Integer numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
        return this;
    }

    public StreamKMClusterer setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public StreamKMClusterer setRandomSeed(Integer randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    public StreamKMClusterer() {
        super((StreamKM) ExternalClusterer.STREAM_KMEANS.newInstance());
    }

    public StreamKMClusterer(boolean alwaysTrain, boolean calculateDistance, Integer sizeCoreset, Integer numberOfClusters, Integer width, Integer randomSeed) {
        super((StreamKM) ExternalClusterer.STREAM_KMEANS.newInstance(), alwaysTrain, calculateDistance);
        this.sizeCoreset = sizeCoreset;
        this.numberOfClusters = numberOfClusters;
        this.width = width;
        this.randomSeed = randomSeed;
    }

    public StreamKMClusterer(Set<String> trainedLabels, Map<String, Object> parameters, boolean calculateDistance, Integer sizeCoreset, Integer numberOfClusters, Integer width, Integer randomSeed) throws IllegalArgumentException {
        super((StreamKM) ExternalClusterer.STREAM_KMEANS.newInstance(), trainedLabels, calculateDistance);
        this.sizeCoreset = sizeCoreset;
        this.numberOfClusters = numberOfClusters;
        this.width = width;
        this.randomSeed = randomSeed;
    }

    @Override
    protected Clustering getClusteringResult() {
        return clusterer.getClusteringResult();
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        int numMetrics = firstSample.getHeader().header.length;
        numMetrics++; // The class/label attribute is added
        IntOption sizeCoresetOption = new IntOption("sizeCoreset",
                's', "Size of the coreset.", sizeCoreset == null ? 1000 : sizeCoreset);
        IntOption numClustersOption = new IntOption(
                "numClusters", 'k',
                "Number of clusters to compute.", numberOfClusters == null ? 5 : numberOfClusters);
        IntOption widthOption = new IntOption("width",
                'w', "Size of Window for training learner.", width == null ? 10000 : width, 0, Integer.MAX_VALUE);
        IntOption randomSeedOption = new IntOption("randomSeed", 'r',
                "Seed for random behaviour of the classifier.", randomSeed == null ? 1 : randomSeed);

        this.clusterer.sizeCoresetOption = sizeCoresetOption;
        this.clusterer.numClustersOption = numClustersOption;
        this.clusterer.widthOption = widthOption;
        this.clusterer.randomSeedOption = randomSeedOption;
    }
}
