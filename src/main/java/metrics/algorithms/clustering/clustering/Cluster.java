package metrics.algorithms.clustering.clustering;

/**
 * A cluster is a general purpose representation of a single Cluster.
 * Different forms of a cluster a possible.
 */
public interface Cluster {

    /**
     * @return the size of the cluster for each dimension. Used to support other shapes than spheres.
     */
    double getSizeInDim(int dimension);

    /**
     * @return the number of dimension in this cluster
     */
    int getNumDimensions();

    /**
     * @return a double array representing a point that is in the center for each dimension.
     */
    double[] getCenter();

    String[] getHeader();

    String getName();

    int getId();
}
