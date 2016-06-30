package metrics;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Represents one vector of data.
 * The header contains labels for the values. It does not contain special fields like
 * {@link Header#HEADER_TIME}, although these fields are also transported over the network.
 */
public class Sample {

    public static final String TAG_SOURCE = "src";
    public static final String TAG_LABEL = "cls";
    public static final String TAG_EQUALS = "=";
    public static final String TAG_SEPARATOR = " ";

    private final Date timestamp;
    private final Header header;
    private final double[] metrics;
    private final Map<String, String> tags;

    public Sample(Header header, double[] metrics, Date timestamp, Map<String, String> tags) {
        this.header = header;
        this.timestamp = timestamp;
        this.metrics = metrics;
        this.tags = tags == null ? new HashMap<>() : tags;
    }

    public Sample(Header header, double[] metrics, Date timestamp) {
        this(header, metrics, timestamp, null);
    }

    public Sample(Header header, double[] metrics, Sample source) {
        this(header, metrics, source.getTimestamp());
        tags.putAll(source.tags);
    }

    public Sample(Header header, double[] metrics, Date timestamp, String source, String label) {
        this(header, metrics, timestamp, null);
        setSource(source);
        setLabel(label);
    }

    public static Sample unmarshallSample(Header header, double[] metrics, Date timestamp, String tags) throws IOException {
        return new Sample(header, metrics, timestamp, parseTags(tags));
    }

    public static Map<String, String> parseTags(String tags) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (tags != null && !tags.isEmpty()) {
            String parts[] = tags.split("[= ]");
            if (parts.length % 2 != 0)
                throw new IOException("Illegal tags string: " + tags);
            for (int i = 0; i < parts.length; i += 2) {
                result.put(parts[i], parts[i + 1]);
            }
        }
        return result;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Header getHeader() {
        return header;
    }

    public double[] getMetrics() {
        return metrics;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTag(String name) {
        return tags.get(name);
    }

    public void deleteTag(String name) {
        tags.remove(name);
    }

    public void setTag(String name, String value) {
        tags.put(name, value);
    }

    public String getSource() {
        return getTag(TAG_SOURCE);
    }

    public boolean hasSource() {
        return getSource() != null;
    }

    public String getLabel() {
        return getTag(TAG_LABEL);
    }

    public boolean hasLabel() {
        return getLabel() != null;
    }

    public boolean headerChanged(Header oldHeader) {
        return header.hasChanged(oldHeader);
    }

    public void setSource(String source) {
        setTag(TAG_SOURCE, source);
    }

    public void setLabel(String label) {
        setTag(TAG_LABEL, label);
    }

    public static String escapeTagString(String tag) {
        return tag.replaceAll("[ =\n,]", "_");
    }

    public String tagString() {
        StringBuilder s = new StringBuilder();
        boolean started = false;
        for (Map.Entry<String, String> tag : tags.entrySet()) {
            String key = escapeTagString(tag.getKey());
            String value = escapeTagString(tag.getValue());
            if (started) s.append(TAG_SEPARATOR);
            s.append(key).append(TAG_EQUALS).append(value);
            started = true;
        }
        return s.toString();
    }

    public void checkConsistency() throws IOException {
        if (header == null)
            throw new IOException("Sample.header is null");
        if (metrics == null)
            throw new IOException("Sample.metrics is null");
        if (timestamp == null)
            throw new IOException("Sample.timestamp is null");
        if (header.header.length != metrics.length)
            throw new IOException("Sample.header is size " + header.header.length +
                    ", but Sample.metrics is size " + metrics.length);
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Sample(");
        boolean started = false;
        if (header != null) {
            b.append(header.header.length).append(" fields");
            started = true;
        }
        if (metrics != null) {
            if (started) b.append(", ");
            b.append(metrics.length).append(" metrics");
            started = true;
        }
        if (timestamp != null) {
            if (started) b.append(", ");
            b.append(timestamp);
        }
        b.append(tagString());
        return b.append(")").toString();
    }

    public static Sample newEmptySample() {
        return new Sample(new Header(new String[0], false), new double[0], new Date());
    }

}
