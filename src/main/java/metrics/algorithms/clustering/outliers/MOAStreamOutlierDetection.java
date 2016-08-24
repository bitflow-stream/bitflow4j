package metrics.algorithms.clustering.outliers;

import com.yahoo.labs.samoa.instances.Instance;
import metrics.Sample;
import metrics.algorithms.clustering.MOAStreamClusterer;
import moa.cluster.Clustering;
import moa.clusterers.outliers.MyBaseOutlierDetector;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T> the class of the moa outlierdetector
 */
public abstract class MOAStreamOutlierDetection<T extends MyBaseOutlierDetector & Serializable> extends MOAStreamClusterer<T>  {


    private final MOAOutlierNotifier outlierNotifier;
    private final HashSet<Long> outlierIds, inlierIds;
    protected volatile Sample currentSample;

    public void recursiion(int n){
        n--;
        if(n == 0) return;
        else recursiion(n); System.out.println("Hello");
    }

    public MOAStreamOutlierDetection(T clusterer, boolean alwaysTrain) {
        super(clusterer, alwaysTrain, false);
        this.outlierIds = new HashSet<>();
        this.outlierNotifier = new MOAOutlierNotifier();
        inlierIds = new HashSet<Long>();
        try{
            ((MyBaseOutlierDetector)clusterer).outlierNotifier = this.outlierNotifier;
        }catch(ClassCastException e){
            throw new IllegalArgumentException("Clusterer must be a subclass of moa.clusterers.outliers.MyBaseOutlierDetector");
        }
    }

    public MOAStreamOutlierDetection(T clusterer, Set<String> trainedLabels) {
        super(clusterer, trainedLabels, false);
        outlierIds = new HashSet<>();
        inlierIds = new HashSet<Long>();
        this.outlierNotifier = new MOAOutlierNotifier();
        try{
            ((MyBaseOutlierDetector)clusterer).outlierNotifier = this.outlierNotifier;
        }catch(ClassCastException e){
            throw new IllegalArgumentException("Clusterer must be a subclass of moa.clusterers.outliers.MyBaseOutlierDetector");
        }
    }

//    @Override
//    protected void setupClustererParameter(Sample firstSample) {
//
//    }


    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        this.currentSample = sample;
        Sample sampleToReturn = super.executeSample(sample);
        System.out.println("clusterId =" + sample.getClusterId());
        return sampleToReturn;
    }

    @Override
    protected Clustering getClusteringResult() {
        return clusterer.getClusteringResult();
    }
    //TODO: distance calculation should only be done for normal sphere clusterers (or can we calc distance for outliers?)
    @Override
    protected Map.Entry<Double, double[]> getDistance(Instance instance, Clustering clustering) throws IOException {
        return null;
    }

    protected abstract void onOutlier(MyBaseOutlierDetector.Outlier outlier);

    protected abstract void onInlier(MyBaseOutlierDetector.Outlier outlier);

    private void processInlier(MyBaseOutlierDetector.Outlier outlier){
//        this.inliers++;
        System.out.println("im here!!!");
        checkPrintReq();
        if(this.outlierIds.remove(outlier.id)){
            //do something with prio outlier
            if(inlierIds.add(outlier.id)){
                //prio outliner now inliner
            }else{
                //prio inliner categorized as inlier again (should not happen)
                throw new IllegalStateException("ERROR: inlier cannot be detected 2x. either processing of outlier or inliers is bugged (probably, the inlier was not removed correctly).");
            }
        } else{
            //do something with inlier, that has not been an outlier before
            if(inlierIds.add(outlier.id)){
                //no prio outlier, should be first categorization as inlier
            }else {
                // double detection should not happen
                throw new IllegalStateException("ERROR: inlier cannot be detected 2x. either processing of outlier or inliers is bugged (probably, the inlier was not removed correctly).");
            }

        }
        //sub-class hook
        onInlier(outlier);

    }

    private void checkPrintReq() {
        if(sampleCount % 100 == 0){
            System.out.println("number of inliers: " + this.inlierIds.size());
            System.out.println("number of outliers: " + this.outlierIds.size());
        }
    }

    private void processOutlier(MyBaseOutlierDetector.Outlier outlier){
        System.out.println("im here!!!");
        checkPrintReq();
        if (this.outlierIds.add(outlier.id)) {
            //TODO: do something with new outlier
        }else {
            //TODO: do something with repeated inlier
        }
        //sub-class hook
        onOutlier(outlier);
    }


    class MOAOutlierNotifier extends MyBaseOutlierDetector.OutlierNotifier {
        public MOAOutlierNotifier(){}

        @Override
        public void OnOutlier(MyBaseOutlierDetector.Outlier outlier) {
            System.out.println("im here first time!!!");
            processOutlier(outlier);
        }

        @Override
        public void OnInlier(MyBaseOutlierDetector.Outlier outlier) {
            System.out.println("im here first time!!!");
            processInlier(outlier);
        }
    }

}
