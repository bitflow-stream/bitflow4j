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

    private volatile Float radiusParameter;
    private volatile Integer kParameter;

    public SCODOutlierDetector(){
        super((SimpleCOD) ExternalClusterer.SCOD.newInstance());
    }

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

    public SCODOutlierDetector setRadius(Float radiusParameter) {
        if(radiusParameter!= null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.radiusParameter = radiusParameter;
            return this;
        }
    }

    public SCODOutlierDetector setK(Integer kParameter) {
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
        if(radiusParameter != null)clusterer.radiusOption.setValue(radiusParameter);
    }
}
