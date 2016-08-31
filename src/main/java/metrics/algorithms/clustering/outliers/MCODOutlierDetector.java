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



    private volatile Float radiusParameter;
    private volatile Integer kParameter;

    public MCODOutlierDetector(){
        super((MCOD) ExternalClusterer.MCOD.newInstance());
        this.radiusParameter = null;
        this.kParameter = null;
    }

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

    public MCODOutlierDetector setRadius(Float radiusParameter) {
        if(radiusParameter!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.radiusParameter = radiusParameter;
            return this;
        }
    }

    public MCODOutlierDetector setK(Integer kParameter) {
        if(kParameter != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.kParameter = kParameter;
            return this;
        }
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
