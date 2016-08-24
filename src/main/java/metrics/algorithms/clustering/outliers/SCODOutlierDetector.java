package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.MyBaseOutlierDetector;
import moa.clusterers.outliers.SimpleCOD.SimpleCOD;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class SCODOutlierDetector extends MOAStreamOutlierDetection<SimpleCOD> {

    private final Float radiusParameter;
    private final Integer kParameter;

    public SCODOutlierDetector(boolean alwaysTrain, Float radius, Integer kParameter) {
        super((SimpleCOD) ExternalClusterer.SCOD.newInstance(), alwaysTrain);
        this.radiusParameter = radius;
        this.kParameter = kParameter;
    }

    public SCODOutlierDetector(Set<String> trainedLabels, Float radius, Integer kParameter) {
        super((SimpleCOD) ExternalClusterer.SCOD.newInstance(),trainedLabels);
        this.radiusParameter = radius;
        this.kParameter = kParameter;
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
        if(radiusParameter != null)clusterer.radiusOption.setValue(radiusParameter);
    }
}
