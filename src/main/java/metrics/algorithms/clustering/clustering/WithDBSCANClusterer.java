package metrics.algorithms.clustering.clustering;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.cluster.Clustering;
import moa.clusterers.denstream.WithDBSCAN;
/**
  * @author mbyfield
 * Implementation of the Denstream clustering algorithm.
  */
public class WithDBSCANClusterer extends MOASphereClusterer<WithDBSCAN> {
    //TODO sanetize setter and null check
    public WithDBSCANClusterer setHorizon(Integer horizon) {
        this.horizon = horizon;
        return this;
    }

    public WithDBSCANClusterer setNumberOfInitPoints(Integer numberOfInitPoints) {
        this.numberOfInitPoints = numberOfInitPoints;
        return this;
    }

    public WithDBSCANClusterer setProcessingSpeed(Integer processingSpeed) {
        this.processingSpeed = processingSpeed;
        return this;
    }

    public WithDBSCANClusterer setEpsilonParameter(Double epsilonParameter) {
        this.epsilonParameter = epsilonParameter;
        return this;
    }

    public WithDBSCANClusterer setMuParameter(Double muParameter) {
        this.muParameter = muParameter;
        return this;
    }

    public WithDBSCANClusterer setOfflineMultiplier(Double offlineMultiplier) {
        this.offlineMultiplier = offlineMultiplier;
        return this;
    }

    private Integer horizon = null;
    private Integer numberOfInitPoints =null;
    private Integer processingSpeed =null;
    private Double epsilonParameter =null;
    private Double muParameter =null;

    private Double offlineMultiplier;
    // TODO: for all clusterers, sanetize input for options

    public WithDBSCANClusterer(){
        super((WithDBSCAN) ClusteringAlgorithm.DENSTREAM.newInstance());
    }

    public WithDBSCANClusterer(boolean calculateDistance, Integer horizon, Integer numberOfInitPoints, Integer processingSpeed, Double epsilonParameter, Double muParameter, Double offlineMultiplier) {
        super((WithDBSCAN) ClusteringAlgorithm.DENSTREAM.newInstance(), calculateDistance);
        this.horizon = horizon;
        this.numberOfInitPoints = numberOfInitPoints;
        this.processingSpeed = processingSpeed;
        this.epsilonParameter = epsilonParameter;
        this.muParameter = muParameter;
        this.offlineMultiplier = offlineMultiplier;
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        IntOption horizonOption =  new IntOption("horizon", 'h',
                "Range of the window.", horizon == null ? 1000 : horizon);
        IntOption initPointsOption = new IntOption("initPoints", 'i',
                "Number of points to use for initialization.", numberOfInitPoints == null ? 1000 : numberOfInitPoints);
        IntOption speedOption = new IntOption("processingSpeed", 's',
                "Number of incoming points per time unit.", processingSpeed == null ? 100 : processingSpeed, 1, 1000);
        FloatOption betaOption = new FloatOption("beta", 'b', "", 0.2, 0,
                1);
        FloatOption epsilonOption = new FloatOption("epsilon", 'e',
                "Defines the epsilon neighbourhood", epsilonParameter == null ? 15 : epsilonParameter, 0, 1);
        FloatOption lambdaOption = new FloatOption("lambda", 'l', "",
                0.25, 0, 1);
        FloatOption muOption = new FloatOption("mu", 'm', "", muParameter == null ? 1 : muParameter, 0,
                Double.MAX_VALUE);
        FloatOption offlineOption =   new FloatOption("offline", 'o',
                "offline multiplier for epsilion.", offlineMultiplier == null ? 2 : offlineMultiplier, 2, 20);
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
