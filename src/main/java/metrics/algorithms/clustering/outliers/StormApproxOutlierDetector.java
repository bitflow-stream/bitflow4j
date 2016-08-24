package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.Angiulli.ApproxSTORM;
import moa.clusterers.outliers.MyBaseOutlierDetector;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class StormApproxOutlierDetector extends MOAStreamOutlierDetection<ApproxSTORM> {
    private final Float radiusParameter;
    private final Integer kParameter;
    private final Integer queryFrequency;
    private final Float pParameter;

    public StormApproxOutlierDetector(boolean alwaysTrain, Float radiusParameter, Integer kParameter, Integer queryFrequency, Float pParameter) {
        super((ApproxSTORM) ExternalClusterer.STORM_APPROX.newInstance(), alwaysTrain);
        this.radiusParameter = radiusParameter;
        this.kParameter = kParameter;
        this.queryFrequency = queryFrequency;
        this.pParameter = pParameter;
    }

    public StormApproxOutlierDetector(Set<String> trainedLabels, Float radiusParameter, Integer kParameter, Integer queryFrequency, Float pParameter) {
        super((ApproxSTORM) ExternalClusterer.STORM_APPROX.newInstance(), trainedLabels);
        this.radiusParameter = radiusParameter;
        this.kParameter = kParameter;
        this.queryFrequency = queryFrequency;
        this.pParameter = pParameter;
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
        if(pParameter != null) clusterer.pOption.setValue(pParameter);
        if(queryFrequency != null) clusterer.queryFreqOption.setValue(queryFrequency);
        if(radiusParameter != null) clusterer.radiusOption.setValue(radiusParameter);
    }
}
