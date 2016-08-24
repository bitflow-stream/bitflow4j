package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.MCOD.MCOD;
import moa.clusterers.outliers.MyBaseOutlierDetector;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class MCODOutlierDetector extends MOAStreamOutlierDetection<MCOD> {

    private final Float radiusParameter;
    private final Integer kParameter;

    public MCODOutlierDetector(boolean alwaysTrain, Float radius, Integer kParameter) {
        super((MCOD) ExternalClusterer.MCOD.newInstance(), alwaysTrain);
        this.radiusParameter = radius;
        this.kParameter = kParameter;
    }


    public MCODOutlierDetector(Set<String> trainedLabels, Float radius, Integer kParameter) {
        super((MCOD) ExternalClusterer.MCOD.newInstance(), trainedLabels);
        this.kParameter = kParameter;
        this.radiusParameter = radius;
    }

    @Override
    protected void onOutlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void onInlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        if(kParameter != null) clusterer.kOption.setValue(kParameter);
        if(radiusParameter != null) clusterer.radiusOption.setValue(radiusParameter);
    }
}
