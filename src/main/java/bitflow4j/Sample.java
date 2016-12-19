package bitflow4j;

import java.io.IOException;
import java.util.*;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Represents one vector of data. The header contains labels for the values.
 * In addition to the values, the Sample also contains a timestamp and a Map of tags (key-value pairs).
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

    // Do not use this constructor to copy another sample, only to create artificial new samples.
    public Sample(Header header, double[] metrics, Date timestamp, Map<String, String> tags) {
        this.header = header;
        this.timestamp = timestamp;
        this.metrics = metrics;
        this.tags = tags == null ? new HashMap<>() : tags;
    }

    // Do not use this constructor to copy another sample, only to create artificial new samples.
    public Sample(Header header, double[] metrics, Date timestamp) {
        this(header, metrics, timestamp, null);
    }

    // Create a copy of the source Sample: the meta data will be copied with a new header and new metrics.
    public Sample(Header header, double[] metrics, Sample source) {
        this(header, metrics, source.getTimestamp(), null);
        if (source.tags != null)
            tags.putAll(source.tags);
    }

    // Create a complete copy of source (reuse the metrics array)
    public Sample(Sample source) {
        this(source.getHeader(), source.getMetrics(), source);
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

    public boolean hasTag(String name) {
        String tag = getTag(name);
        return tag != null && !tag.isEmpty();
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
        return hasTag(TAG_LABEL);
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

    public int getIntTag(String tag) throws IOException {
        int val;
        try {
            val = Integer.parseInt(getTag(tag));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("Failed to convert tag " + tag + " to int: " + e);
        }
        return val;
    }

    public static String escapeTagString(String tag) {
        if (tag == null) {
            return "NULL";
        }
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

    public Sample extend(String[] newFields, double newValues[]) {
        if (newFields.length != newValues.length) {
            throw new IllegalArgumentException("Need equal number of new fields and values");
        }

        // Extend Header
        int incomingFields = getHeader().header.length;
        String[] headerNames = Arrays.copyOf(getHeader().header, incomingFields + newFields.length);
        System.arraycopy(newFields, 0, headerNames, incomingFields, newFields.length);
        Header outHeader = new Header(headerNames, getHeader());

        // Extend Metrics
        double[] outMetrics = Arrays.copyOf(getMetrics(), headerNames.length);
        System.arraycopy(newValues, 0, outMetrics, incomingFields, newValues.length);

        return new Sample(outHeader, outMetrics, this);
    }

    /**
     * Return a copy of the receiver with all given metrics removed.
     * If the list of metrics would stay the same, the received is returned unchanged.
     */
    public Sample removeMetrics(Collection<String> removeMetrics) {
        //TODO performance check
        String headerFields[] = getHeader().header;
        double metrics[] = getMetrics();
        double newMetrics[] = new double[headerFields.length];
        String newHeaderFields[] = new String[headerFields.length];
        int j = 0;
        for (int i = 0; i < headerFields.length; i++) {
            String headerField = headerFields[i];
            if (!removeMetrics.contains(headerField)) {
                newHeaderFields[j] = headerField;
                newMetrics[j] = metrics[i];
                j++;
            }
        }
        if (j == metrics.length) {
            return this;
        } else {
            newMetrics = Arrays.copyOf(newMetrics, j);
            newHeaderFields = Arrays.copyOf(newHeaderFields, j);
            return new Sample(new Header(newHeaderFields, getHeader()), newMetrics, this);
        }
    }

    public Sample removeMetrics(String... metrics) {
        return removeMetrics(new HashSet<>(Arrays.asList(metrics)));
    }

    public Sample removeMetricsWithPrefix(String prefix) {
        Set<String> removeMetrics = new HashSet<>();
        for (String metric : getHeader().header) {
            if (metric.startsWith(prefix)) {
                removeMetrics.add(metric);
            }
        }
        return removeMetrics(removeMetrics);
    }

}
