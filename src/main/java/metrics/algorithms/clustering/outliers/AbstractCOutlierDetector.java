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
    //TODO maybe add some more secure synchronization like a readwritelock or just throw exception after first set
    private volatile Float radiusParameter;
    private volatile Integer kParameter;
    private volatile Boolean waitWinFullFlag;

    public AbstractCOutlierDetector setRadius(Float radiusParameter) throws  IllegalStateException{
        if (this.radiusParameter == null) {
            this.radiusParameter = radiusParameter;
            return this;
        }
        else throw new IllegalStateException("Final option cannot be set twice.");
    }

    public AbstractCOutlierDetector setK(Integer kParameter) throws IllegalStateException{
        if(kParameter != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.kParameter = kParameter;
            return this;
        }
    }

    public AbstractCOutlierDetector setWaitWinFullFlag(Boolean waitWinFullFlag) {

        if(waitWinFullFlag != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.waitWinFullFlag = waitWinFullFlag;
            return this;
        }
    }

    public AbstractCOutlierDetector(){
        super ((AbstractC) ExternalClusterer.ABSTRACT_C.newInstance());
        radiusParameter = null;
        kParameter = null;
        waitWinFullFlag = null;
    }

    public AbstractCOutlierDetector(boolean alwaysTrain, Float radiusParameter, Integer k, Boolean waitWinFullFlag ) {
//        super((AbstractC) ExternalClusterer.ABSTRACT_C.newInstance(), alwaysTrain);
        this();
        this.alwaysTrain = alwaysTrain;
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
