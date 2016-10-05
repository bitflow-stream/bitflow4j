package metrics.algorithms.clustering.clustering;

/**
 * Created by Malcolm-X on 03.10.2016.
 */
public class SphereCluster implements Cluster {
    private double[] center;
    private double radius;
    private String[] header;
    private String name;
    private int id;

    public SphereCluster(double[] center, double radius, String[] header, String name, int id) {
        if (center.length != header.length) {
            throw new IllegalArgumentException("header and center must have same size! Found: header: "
                    + header.length + " center: " + center.length);
        }
        this.name = name;
        this.id = id;
        this.center = center;
        this.radius = radius;
        this.header = header;
    }

    public SphereCluster(double[] center, double radius, String[] header, int id) {
        this(center, radius, header, String.valueOf(id), id);
    }


    @Override
    public double getSizeInDim(int dimension) {
        return this.radius;
    }

    public double getRadius() {
        return this.radius;
    }

    @Override
    public int getNumDimensions() {
        return this.center.length;
    }

    @Override
    public double[] getCenter() {
        return this.center;
    }

    @Override
    public String[] getHeader() {
        return this.header;
    }

    @Override
    public String getName() {
        return this.name == null || this.name.isEmpty() ? String.valueOf(id) : name;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
