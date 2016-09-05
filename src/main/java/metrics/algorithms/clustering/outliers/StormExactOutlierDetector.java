package metrics.algorithms.clustering.outliers;

import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.clusterers.outliers.Angiulli.ExactSTORM;
import moa.clusterers.outliers.MyBaseOutlierDetector;

/**
 * Created by malcolmx on 23.08.16.
 */
public class StormExactOutlierDetector extends MOAStreamOutlierDetection<ExactSTORM> {
    private volatile Float radiusParameter;
    private volatile Integer kParameter;
    private volatile Integer queryFrequency;

    public StormExactOutlierDetector(Float radiusParameter, Integer kParameter, Integer queryFrequency) {
        super((ExactSTORM) ClusteringAlgorithm.STORM_EXACT.newInstance());
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

    public StormExactOutlierDetector setQueryFrequency(Integer queryFrequency) {
        if(queryFrequency!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.queryFrequency = queryFrequency;
            return this;
        }
    }

    public StormExactOutlierDetector setRadius(Float radiusParameter) {
        if(radiusParameter!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.radiusParameter = radiusParameter;
            return this;
        }
    }

    public StormExactOutlierDetector setK(Integer kParameter) {
        if(kParameter != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.kParameter = kParameter;
            return this;
        }
    }
}
