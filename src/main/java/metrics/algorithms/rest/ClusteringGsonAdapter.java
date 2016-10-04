package metrics.algorithms.rest;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import moa.cluster.Cluster;
import moa.core.AutoExpandVector;

import java.io.IOException;

/**
 * Gson adapter for the Clustering as required by the visualisation engine.
 */
public class ClusteringGsonAdapter extends TypeAdapter<AutoExpandVector<Cluster>>{



    private final String[] header;

    public ClusteringGsonAdapter(String[] header) {
        this.header = header;
    }



    @Override
    public void write(JsonWriter jsonWriter, AutoExpandVector<Cluster> clusters) throws IOException {
        for (Cluster cluster : clusters) {
            double[] valuesForCluster;
            jsonWriter.beginObject().name("data").beginArray();
            valuesForCluster = cluster.getCenter();
        }
        jsonWriter.endArray().name("header").beginArray();
        for(String headerField : this.header){
            jsonWriter.value(headerField);
        }
        jsonWriter.endArray().endObject();
    }

    @Override
    public AutoExpandVector<Cluster> read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
