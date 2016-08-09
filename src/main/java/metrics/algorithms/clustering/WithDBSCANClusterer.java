package metrics.algorithms.clustering;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import metrics.Sample;
import moa.cluster.Clustering;
import moa.clusterers.denstream.WithDBSCAN;

import java.util.Map;
import java.util.Set;

public class WithDBSCANClusterer extends MOASphereClusterer<WithDBSCAN> {
//    TODO: add parameters for options

    public WithDBSCANClusterer(boolean alwaysTrain, boolean calculateDistance) {
        super((WithDBSCAN) ExternalClusterer.DENSTREAM.newInstance(), alwaysTrain, calculateDistance);
    }

    public WithDBSCANClusterer(Set<String> trainedLabels, Map<String, Object> parameters, boolean calculateDistance) throws IllegalArgumentException {
        super((WithDBSCAN) ExternalClusterer.DENSTREAM.newInstance(), trainedLabels, calculateDistance);
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        int numMetrics = firstSample.getHeader().header.length;
        numMetrics++; // The class/label attribute is added
        IntOption horizonOption = new IntOption("horizon", 'h',
                "Range of the window.", 1000);
        IntOption initPointsOption = new IntOption("initPoints", 'i',
                "Number of points to use for initialization.", 1000);
        IntOption speedOption = new IntOption("processingSpeed", 's',
                "Number of incoming points per time unit.", 100, 1, 1000);
        FloatOption betaOption = new FloatOption("beta", 'b', "", 0.2, 0,
                1);
        FloatOption epsilonOption = new FloatOption("epsilon", 'e',
                "Defines the epsilon neighbourhood", 0.15, 0, 1);
        FloatOption lambdaOption = new FloatOption("lambda", 'l', "",
                0.25, 0, 1);
        FloatOption muOption = new FloatOption("mu", 'm', "", 1, 0,
                Double.MAX_VALUE);
        FloatOption offlineOption = new FloatOption("offline", 'o',
                "offline multiplier for epsilion.", 2, 2, 20);
        this.clusterer.horizonOption = horizonOption;
        this.clusterer.initPointsOption = initPointsOption;
        this.clusterer.speedOption = speedOption;
        this.clusterer.betaOption = betaOption;
        this.clusterer.lambdaOption = lambdaOption;
        this.clusterer.epsilonOption = epsilonOption;
        this.clusterer.muOption = muOption;
        this.clusterer.offlineOption = offlineOption;
    }

    @Override
    protected Clustering getClusteringResult() {
        return clusterer.getClusteringResult();
    }
}
