package metrics.algorithms.clustering.clustering;


import java.util.List;

/**
 * Created by Malcolm-X on 30.09.2016.
 */
public interface Clustering {
    public void addAll(List<Cluster> clusters) throws IllegalArgumentException;

    public void add(Cluster c) throws IllegalArgumentException;

    /**
     * Get the header array for all Clusters. The header array and the cluster values must have the same size.
     *
     * @return
     */
    public String[] getHeader();

    /**
     * The current Clustering.
     *
     * @return The current clustering represented by a Colection of double arrays. Each array represents the center of Cluster in n-dimensional space.
     */
    public List<Cluster> getClusters();



    public String getGraphJson();
}
