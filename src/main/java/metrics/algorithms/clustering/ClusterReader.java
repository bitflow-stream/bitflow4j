package metrics.algorithms.clustering;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.io.MetricOutputStream;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.cluster.SphereCluster;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class can be used to read a {@link moa.cluster.Clustering} (e.g. extracted from the model of a MOA Clustering Algorithm.
 */
public class ClusterReader extends AbstractAlgorithm{

    private final ClustererAccessor accessor;

    private boolean useMicroClusters = false;
    private String[] headerStrings;
    private boolean addRadius = false;

    public ClusterReader(ClustererAccessor accessor) {
        if (accessor == null) {
            throw new IllegalArgumentException("Empty or null clustering not allowed.");
        }
        this.accessor = accessor;
    }

    public ClusterReader(AbstractClusterer clusterer) throws IllegalArgumentException{
        this(() -> clusterer);
    }

    protected Sample executeSample(Sample sample) throws IOException {
        return null; // Don't forward samples, only output results when input stream is closed
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        try {
            AbstractClusterer clusterer = accessor.getClusterer();
            Clustering clustering = useMicroClusters ? clusterer.getMicroClusteringResult() : clusterer.getClusteringResult();
            List<Sample> samples = getSamplesFromClustering(clustering);
            for (Sample s : samples)
                output.writeSample(s);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("underlying clustering contains Cluster that does not an instance of SphereClusterer");
        }catch (NullPointerException e){
            throw new IllegalStateException("underlying clusterer returned null on call to " + (useMicroClusters ? "getMicroClusteringResult()" : "getClusteringResult()" ));
        }
        catch (Exception e){
            throw new IllegalStateException("underlying clusterer returned null on call to " + (useMicroClusters ? "getMicroClusteringResult()" : "getClusteringResult()"), e);
        } catch (Exception e) {
            throw new IllegalStateException("underlying cluster threw exception on call to " + (useMicroClusters ? "getMicroClusteringResult()" : "getClusteringResult()" ), e);
        }
    }

    public ClusterReader useMicroClusters(){
        this.useMicroClusters = true;
        return this;
    }

    public ClusterReader addRadius(){
        this.addRadius = true;
        return this;
    }

    private List<Sample> getSamplesFromClustering(Clustering clustering) throws IllegalArgumentException{
        setHeaderArray();
        List<Sample> result = new ArrayList<>();
        if (clustering.size() == 0) {
            throw new IllegalArgumentException("empty clustering");
        }
        for(Cluster cluster : clustering.getClustering()){
//            result.addAll(getSamplesFromCluster(cluster));
            result.add(getSampleFromCluster(cluster));
        }
        return result;
    }

    private void setHeaderArray() {
        AbstractClusterer clusterer = accessor.getClusterer();
        int num = clusterer.getModelContext().numAttributes();
        this.headerStrings = new String[num + (addRadius ? 1 : 0)];
        for(int i = 0; i < num; i++){
            headerStrings[i] = clusterer.getModelContext().attribute(i).name();
        }
        if (addRadius)
            headerStrings[headerStrings.length - 1] = "radius";
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
        //TODO: how to reduce dimension
        return new Sample(header, metrics, new Date());
    }

    private double[] getMetrics(SphereCluster sphereCluster) {
        double[] result;
        if ( addRadius ) {
            double[] center = sphereCluster.getCenter();
            result = new double[center.length + 1];
            System.arraycopy(center, 0, result, 0, center.length);
            result[result.length-1] = sphereCluster.getRadius();
        }else{
            result = sphereCluster.getCenter();
        }

        return result;
    }

    public interface ClustererAccessor {
        AbstractClusterer getClusterer();
    }

}
