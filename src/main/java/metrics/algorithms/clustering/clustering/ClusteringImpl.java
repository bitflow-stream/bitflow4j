package metrics.algorithms.clustering.clustering;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Malcolm-X on 04.10.2016.
 */
public class ClusteringImpl implements Clustering {

    private String[] header;
    private List<Cluster> clusters;

    //TODO move all occurences of Gson to one class (e.g. JsonUtils)
    private Gson gson;

    public ClusteringImpl(String[] header) {
        this.header = header;
        this.gson = new GsonBuilder().registerTypeAdapter(Cluster.class, new ClusterTypeAdapter()).create();
        this.clusters = new LinkedList<Cluster>();
    }

    public ClusteringImpl(String[] header, List<Cluster> clusters) {
        this(header);
        this.addAll(clusters);
    }

    @Override
    public void addAll(List<Cluster> clusters) throws IllegalArgumentException {
        for (Cluster c : clusters) add(c);
    }

    @Override
    public void add(Cluster c) throws IllegalArgumentException {
//        if (this.header.equals(c.getHeader())) clusters.add(c);
        if (Arrays.equals(this.header, c.getHeader())) clusters.add(c);
//        if (this.header.length == c.getHeader().length) clusters.add(c);
            //TODO find solution for non matching headers (blame converger)
        else throw new IllegalArgumentException("Invalid headers," +
                " Clustering and Cluster have different headers! cluster: "
                + Arrays.toString(c.getHeader()) + " clustering: " + Arrays.toString(getHeader()));
    }

    @Override
    public String[] getHeader() {
        return header;
    }

    @Override
    public List<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public String getGraphJson() {
        Cluster[] clusterArray = new Cluster[clusters.size()];
        clusterArray = clusters.toArray(clusterArray);
        String result = gson.toJson(clusterArray, new TypeToken<Cluster[]>() {
        }.getType());
        return result;
    }

    public static class ClusterTypeAdapter extends TypeAdapter<Cluster> {

        @Override
        public void write(JsonWriter jsonWriter, Cluster cluster) throws IOException {
            jsonWriter.beginObject().name("name").value(cluster.getName());
            int numDimensions = cluster.getHeader().length;
            String[] header = cluster.getHeader();
            double[] center = cluster.getCenter();
            for (int i = 0; i < numDimensions; i++) {
                jsonWriter.name(header[i]).value(center[i]);
            }
            jsonWriter.endObject();
        }

        @Override
        public Cluster read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }
}
