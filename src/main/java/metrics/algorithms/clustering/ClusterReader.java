package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.cluster.SphereCluster;
import moa.clusterers.AbstractClusterer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class can be used to read a {@link moa.cluster.Clustering} (e.g. extracted from the model of a MOA Clustering Algorithm.
 */
public class ClusterReader extends AbstractAlgorithm{
    private final AbstractClusterer clusterer;
    private Clustering clustering = null;
    private boolean useMicroClusters = false;
    private String[] headerStrings;
    private boolean addRadius = false;

    public ClusterReader(AbstractClusterer clusterer) throws IllegalArgumentException{
        if(clusterer == null){
            throw new IllegalArgumentException("Empty or null clustering not allowed.");
        }
        this.clusterer = clusterer;
    }

    @Override
    public synchronized void start(MetricInputStream input, MetricOutputStream output) throws IllegalStateException {
//        super.start(input, output);
        //prevent forwarding of any sample
        try {
            this.clustering = useMicroClusters ? clusterer.getMicroClusteringResult() : clusterer.getClusteringResult();
            List<Sample> samplesFromClustering = getSamplesFromClustering();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("underlying clustering contains Cluster that does not an instance of SphereClusterer");
        }catch (NullPointerException e){
            throw new IllegalStateException("underlying clusterer returned null on call to " + (useMicroClusters ? "getMicroClusteringResult()" : "getClusteringResult()" ));
        }catch (Exception e){
            throw new IllegalStateException("underlying cluster threw exception on call to " + (useMicroClusters ? "getMicroClusteringResult()" : "getClusteringResult()" ), e);
        }
    }

    public void useMicroClusters(){
        this.useMicroClusters = true;
    }

    public void addRadius(){
        this.addRadius = true;
    }

    private List<Sample> getSamplesFromClustering() throws IllegalArgumentException{
        setHeaderArray();
        List<Sample> result = new ArrayList<>();

        for(Cluster cluster : clustering.getClustering()){
//            result.addAll(getSamplesFromCluster(cluster));
            result.add(getSampleFromCluster(cluster));
        }
        return result;
    }

    private void setHeaderArray() {
        this.headerStrings = new String[clusterer.getModelContext().numInputAttributes() + (addRadius ? 1 : 0)];
        for(int i = 0; i < headerStrings.length ; i++){
            headerStrings[i] = clusterer.getModelContext().inputAttribute(i).name();
        }
    }

    private Sample getSampleFromCluster(Cluster cluster) throws IllegalArgumentException{
        Date timestamp = new Date();
        SphereCluster sphereCluster;
        try {
            sphereCluster = (SphereCluster)  cluster;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
        Header header = new Header(headerStrings);
        double[] metrics = getMetrics(sphereCluster);
        double[] clusterCenter = sphereCluster.getCenter();
        //TODO: how to reduce dimension
        Sample result = new Sample(header, clusterCenter, new Date());
        return result;
    }

    private double[] getMetrics(SphereCluster sphereCluster) {
        double[] result;
        if ( addRadius ) {
            result = new double[sphereCluster.getCenter().length + 1];
            System.arraycopy(sphereCluster.getCenter(), 0, result, 0, sphereCluster.getCenter().length);
            result[result.length-1] = sphereCluster.getRadius();
        }else{
            result = sphereCluster.getCenter();
        }

        return result;
    }


    //TODO obsolete if we change the plotter
//    private Collection<? extends Sample> getSamplesFromCluster(Cluster cluster) {
//        List<Sample> result = new ArrayList<Sample>();
//
//        return result;
//    }


}
