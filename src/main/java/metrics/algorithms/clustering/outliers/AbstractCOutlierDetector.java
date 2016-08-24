package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.AbstractC.AbstractC;
import moa.clusterers.outliers.MyBaseOutlierDetector;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class AbstractCOutlierDetector extends MOAStreamOutlierDetection<AbstractC> {
    private final Float radiusParameter;
    private final Integer kParameter;
    private final Boolean waitWinFullFlag;

    public AbstractCOutlierDetector(boolean alwaysTrain, Float radiusParameter, Integer k, Boolean waitWinFullFlag ) {
        super((AbstractC) ExternalClusterer.ABSTRACT_C.newInstance(), alwaysTrain);
        this.radiusParameter = radiusParameter;
        this.kParameter = k;
        this.waitWinFullFlag = waitWinFullFlag;
    }

    public AbstractCOutlierDetector(Set<String> trainedLabels, Float radiusParameter, Integer k, Boolean waitWinFullFlag ) {
        super((AbstractC) ExternalClusterer.ABSTRACT_C.newInstance(), trainedLabels);
        this.radiusParameter = radiusParameter;
        this.kParameter = k;
        this.waitWinFullFlag = waitWinFullFlag;
    }

    @Override
    protected void onOutlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void onInlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        if(radiusParameter != null) clusterer.radiusOption.setValue(radiusParameter);
        if(kParameter != null) clusterer.kOption.setValue(kParameter);
        if(waitWinFullFlag != null) clusterer.waitWinFullOption.setValue(waitWinFullFlag);
    }
}
