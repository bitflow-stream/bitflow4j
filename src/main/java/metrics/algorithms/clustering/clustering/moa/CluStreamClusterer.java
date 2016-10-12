package metrics.algorithms.clustering.clustering.moa;

import com.github.javacliparser.IntOption;
import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.clusterers.clustream.Clustream;

/**
 * @author mbyfield
 * Implementation of the Clustream clustering algorithm.
 */
public class CluStreamClusterer extends MOASphereClusterer<Clustream> {

    private Integer kernelRadiusMultiplier = null;
    private Integer numberOfKernels = null;
    private Integer horizon = null;

    public CluStreamClusterer(){
        super((Clustream) ClusteringAlgorithm.CLUSTREAM.newInstance());
    }

    public CluStreamClusterer(boolean calculateDistance, Integer kernelRadiusMultiplier, Integer numberOfKernels, Integer horizon) {
        super((Clustream) ClusteringAlgorithm.CLUSTREAM.newInstance(), calculateDistance);
        this.kernelRadiusMultiplier = kernelRadiusMultiplier;
        this.numberOfKernels = numberOfKernels;
        this.horizon = horizon;
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        int numMetrics = firstSample.getHeader().header.length;
        numMetrics++; // The class/label attribute is added
        IntOption timeWindowOption = new IntOption("horizon",
                'h', "Rang of the window.", horizon == null ? 1000 : horizon);
        IntOption maxNumKernelsOption = new IntOption(
                "maxNumKernels", 'k',
                "Maximum number of micro kernels to use.", numberOfKernels == null ? 100 : numberOfKernels);
        IntOption kernelRadiFactorOption = new IntOption(
                "kernelRadiFactor", 't',
                "Multiplier for the kernel radius", kernelRadiusMultiplier == null ? 2 : kernelRadiusMultiplier);
        this.clusterer.timeWindowOption = timeWindowOption;
        this.clusterer.maxNumKernelsOption = maxNumKernelsOption;
        this.clusterer.kernelRadiFactorOption = kernelRadiFactorOption;
    }
}