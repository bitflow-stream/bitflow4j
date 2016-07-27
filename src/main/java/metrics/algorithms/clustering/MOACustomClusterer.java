//package metrics.algorithms.clustering;
//
//import metrics.Header;
//import metrics.Sample;
//import moa.cluster.*;
//import moa.clusterers.AbstractClusterer;
//import weka.core.Instances;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.Set;
//
///**
// * Created by Malcolm-X on 27.07.2016.
// */
//public class MOACustomClusterer<T extends AbstractClusterer & Serializable> extends MOAStreamClusterer<T> {
//
//    public MOACustomClusterer(T clusterer, boolean alwaysTrain, boolean calculateDistance) throws Exception{
//        super(clusterer, alwaysTrain);
//        if (!(clusterer instanceof  BICOCustom)) {
//            throw new Exception();
//        }else{
//            ((BICOCustom)this.clusterer).setCalculateNoiseDistance(calculateDistance);
//        }
//    }
//
//
//
//    public MOACustomClusterer(T clusterer, Set<String> trainedLabels, int bico_num_clusters, boolean calculateDistance) throws Exception {
//        super(clusterer, trainedLabels, bico_num_clusters);
//        if (!(clusterer instanceof  BICOCustom)) {
//            throw new Exception();
//        }else{
//            ((BICOCustom)this.clusterer).setCalculateNoiseDistance(calculateDistance);
//        }
//    }
//
//    @Override
//    protected synchronized Sample executeSample(Sample sample) throws IOException {
//        Sample result = super.executeSample(sample);
//        Header expectedHeader = converger.getExpectedHeader();
//        String label = getLabel(sample);
//        double values[] = converger.getValues(sample);
//        Instances instances = createInstances(expectedHeader, label);
//        com.yahoo.labs.samoa.instances.Instance instance = makeInstance(values, label, instances);
//        if(result.getTag(ClusterConstants.TRAINING_TAG) != null){
//            ((BICOCustom)this.clusterer).getDistances(instance);
//        }
//        return result;
//    }
//
//    @Override
//    protected void subclassCallback(Clustering clusteringResult) {
//        double[] closestCenter;
//        int closestCluster;
//        int closestRadius;
//        for (moa.cluster.Cluster c : clusteringResult.getClustering()){
//
//        }
//        System.out.println("right callback");
//    }
//}
