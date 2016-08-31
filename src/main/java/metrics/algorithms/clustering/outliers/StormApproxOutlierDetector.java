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
    private volatile Float radiusParameter;
    private volatile Integer kParameter;
    private volatile Integer queryFrequency;
    private volatile Float pParameter;

    public StormApproxOutlierDetector(){
        super((ApproxSTORM) ExternalClusterer.STORM_APPROX.newInstance());
    }

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

    public StormApproxOutlierDetector setP(Float pParameter) {
        if(pParameter!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.pParameter = pParameter;
            return this;
        }
    }

    public StormApproxOutlierDetector setQueryFrequency(Integer queryFrequency) {
        if(queryFrequency!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.queryFrequency = queryFrequency;
            return this;
        }
    }

    public StormApproxOutlierDetector setRadius(Float radiusParameter) {
        if(radiusParameter!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.radiusParameter = radiusParameter;
            return this;
        }
    }

    public StormApproxOutlierDetector setK(Integer kParameter) {
        if(kParameter != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.kParameter = kParameter;
            return this;
        }
    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        if(kParameter != null) clusterer.kOption.setValue(kParameter);
        if(pParameter != null) clusterer.pOption.setValue(pParameter);
        if(queryFrequency != null) clusterer.queryFreqOption.setValue(queryFrequency);
        if(radiusParameter != null) clusterer.radiusOption.setValue(radiusParameter);
    }
}
