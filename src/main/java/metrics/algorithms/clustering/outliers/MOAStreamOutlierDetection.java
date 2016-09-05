package metrics.algorithms.clustering.outliers;

import com.yahoo.labs.samoa.instances.Instance;
import metrics.Sample;
import metrics.algorithms.clustering.MOAStreamClusterer;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.outliers.MyBaseOutlierDetector;
import moa.core.AutoExpandVector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <T> the class of the moa outlierdetector
 */
public abstract class MOAStreamOutlierDetection<T extends MyBaseOutlierDetector & Serializable> extends MOAStreamClusterer<T>  {


    private final MOAOutlierNotifier outlierNotifier;
    private final HashSet<Long> outlierIds = new HashSet<>();
    private final HashSet<Long> inlierIds = new HashSet<>();
    protected Sample currentSample = null;
    protected boolean calculateDistance;
    private boolean alwaysAddDistanceMetrics = false;

    public MOAStreamOutlierDetection(T clusterer){
        super(clusterer);
        this.outlierNotifier = new MOAOutlierNotifier();
        clusterer.outlierNotifier = this.outlierNotifier;
    }

    public MOAStreamOutlierDetection notify(MyBaseOutlierDetector.OutlierNotifier notifier){
        this.outlierNotifier.addOutlierNotifier(notifier);
        return this;
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        this.currentSample = sample;
        Sample sampleToReturn = super.executeSample(sample);
//        System.out.println("clusterId =" + sample.getClusterId());
        return sampleToReturn;
    }

    @Override
    protected Clustering getClusteringResult() {
        return clusterer.getClusteringResult();
    }
    //TODO: distance calculation should only be done for normal sphere clusterers (or can we calc distance for outliers?)
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
        private List<MyBaseOutlierDetector.OutlierNotifier> outlierNotifierList;
        //TODO sync this ? maybe not necessary
        public MOAOutlierNotifier(){
            this.outlierNotifierList = new ArrayList<>();
        }

        @Override
        public void OnOutlier(MyBaseOutlierDetector.Outlier outlier) {
            System.out.println("im here first time!!!");
            this.outlierNotifierList.forEach( notifier -> notifier.OnOutlier(outlier));
            processOutlier(outlier);
        }

        @Override
        public void OnInlier(MyBaseOutlierDetector.Outlier outlier) {
            System.out.println("im here first time!!!");
            this.outlierNotifierList.forEach( notifier -> notifier.OnInlier(outlier));
            processInlier(outlier);
        }

        void addOutlierNotifier(MyBaseOutlierDetector.OutlierNotifier outlierNotifier){
            this.outlierNotifierList.add(outlierNotifier);
        }
    }

    /**
     * Returns the clusterId for the provided instance. For outlier detection, there are only 2 possible id's. -1 indicates an outlier, while other points will have Integer.MAX_VALUE as cluster id.
     * @param instance the instance for the current sample
     * @return -1 for outlier, Integer.MAX_VALUE else.
     */
    @Override
    protected int calculateCluster(Instance instance) {
        //if instance is not found, we return Integer.MAX_VALUE, to indicate this is not an outlier
        if (clusteringResult == null) return Integer.MAX_VALUE;
        AutoExpandVector<Cluster> clustering = clusteringResult.getClustering();
        double inclusionProbability = 0.0;
        //if instance is not found, we return Integer.MAX_VALUE, to indicate this is not an outlier
        int bestFitCluster = Integer.MAX_VALUE;
        int clusterNum = 0;
        // for outlier detection, if an instance is contained in the clustering, it is an outlier
        if(clustering.contains(instance)) bestFitCluster = -1;
//        for (Cluster c : clustering) {
//            double clusterInclusionProbability = c.getInclusionProbability(instance);
//            if (inclusionProbability < clusterInclusionProbability) {
//                inclusionProbability = clusterInclusionProbability;
//                bestFitCluster = clusterNum;
//            }
//            clusterNum++;
//        }
        return bestFitCluster;
    }

}
