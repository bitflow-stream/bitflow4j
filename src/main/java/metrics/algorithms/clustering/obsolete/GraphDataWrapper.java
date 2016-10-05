package metrics.algorithms.clustering.obsolete;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;

/**
 * This class holds data that should be visualised as a graph by the GraphWebServer. A dataset represents one datapoint in
 */
public class GraphDataWrapper {
    /**
     * The name that will be displayed for this value set.
     */
    public String name;

    public double size;
    /**
     * The values, all data objects in one graph must have the same value format (same keys and ordering).
     * The key Strings will be used in the
     */
    public LinkedHashMap<String, Double> values;

    public GraphDataWrapper() {
        this.values = new LinkedHashMap<>();
    }

    public boolean addProperty(String name, Double value) {
        return values.putIfAbsent(name, value) == null;
    }

    public static class GraphTypeAdapter extends TypeAdapter<GraphDataWrapper> {

        @Override
        public void write(JsonWriter jsonWriter, GraphDataWrapper graphDataWrapper) throws IOException {
            jsonWriter.beginObject().name("name").value(graphDataWrapper.name).name("size").value(graphDataWrapper.size);
            graphDataWrapper.values.entrySet().forEach(stringDoubleEntry -> {
                try {
                    jsonWriter.name(stringDoubleEntry.getKey()).value(stringDoubleEntry.getValue());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            jsonWriter.endObject();
        }

        @Override
        public GraphDataWrapper read(JsonReader jsonReader) throws IOException {
            //TODO
            return null;
        }
    }
}
