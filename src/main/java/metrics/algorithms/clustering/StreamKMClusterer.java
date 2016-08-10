package metrics.algorithms.clustering;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import moa.cluster.Clustering;
import moa.clusterers.streamkm.StreamKM;

import java.util.Map;
import java.util.Set;

public class StreamKMClusterer extends MOASphereClusterer<StreamKM> {
    private final Integer sizeCoreset;
    private final Integer numberOfClusters;
    private final Integer width;
    private final Integer randomSeed;

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