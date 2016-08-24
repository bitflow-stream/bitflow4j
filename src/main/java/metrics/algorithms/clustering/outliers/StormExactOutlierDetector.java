package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.Angiulli.ExactSTORM;
import moa.clusterers.outliers.MyBaseOutlierDetector;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class StormExactOutlierDetector extends MOAStreamOutlierDetection<ExactSTORM> {
    private final Float radiusParameter;
    private final Integer kParameter;
    private final Integer queryFrequency;

    public StormExactOutlierDetector(boolean alwaysTrain, Float radiusParameter, Integer kParameter, Integer queryFrequency) {
        super((ExactSTORM) ExternalClusterer.STORM_EXACT.newInstance(), alwaysTrain);
        this.radiusParameter = radiusParameter;
        this.kParameter = kParameter;
        this.queryFrequency = queryFrequency;
    }

    public StormExactOutlierDetector(Set trainedLabels, Float radiusParameter, Integer kParameter, Integer queryFrequency) {
        super((ExactSTORM) ExternalClusterer.STORM_EXACT.newInstance(), trainedLabels);
        this.radiusParameter = radiusParameter;
        this.kParameter = kParameter;
        this.queryFrequency = queryFrequency;
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
        if(queryFrequency != null) clusterer.queryFreqOption.setValue(queryFrequency);
        if(radiusParameter != null) clusterer.radiusOption.setValue(radiusParameter);
    }
}
