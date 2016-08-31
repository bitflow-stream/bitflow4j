package metrics;

import metrics.algorithms.clustering.ClusterConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Represents one vector of data.
 * The header contains labels for the values. It does not contain special fields like
 * {@link Header#HEADER_TIME}, although these fields are also transported over the network.
 */
public class Sample {
    //TODO add some synchronization?


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
//TODO remove
//        if(name == null || tags == null || tags.isEmpty() || tags.get(name) == null || tags.get(name).equalsIgnoreCase("null")){
//            System.out.println("break");
//        }
//        String s = tags.get(name);
//        if(s == null){
//            System.out.println("b");
//        }else if(s.isEmpty()){
//            System.out.println("b");
//        }else if(s.equalsIgnoreCase("null")){
//            System.out.println("b");
//        }
        return tags.get(name);
    }

    public void deleteTag(String name) {
        tags.remove(name);
    }

    public void setTag(String name, String value) {
//        if(value == null || value.isEmpty() || value.equalsIgnoreCase("null"))
//        if(name == null || tags == null){
//            System.out.println("break");
//        }TODO remove
//        String result =
                tags.put(name, value);
//        if (result != null){
//            System.out.println("break");
//        }
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
//        if(label == null || label.isEmpty() || label.equalsIgnoreCase("null")){
//            System.out.println("b");
//        }TODO remove
        setTag(TAG_LABEL, label);
    }

    public int getClusterId() throws IOException {
        int labelClusterId;
        try {
            labelClusterId = Integer.parseInt(getTag(ClusterConstants.CLUSTER_TAG));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException(
                    "Sample not prepared for labeling, add a clusterer to the pipeline or fix current clusterer"+
                            " (failed to extract cluster id from point label or original label not found).");
        }
        return labelClusterId;
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

    //TODO comment and maybe add return type
    public void removeMetrics(String ... metricNames){
        //TODO it will me more officient to have reimplement this (will save multiple loops over the full metrics array
//        for (String s : metricNames) removeMetric(s);
        //cannot call because sample is returned instead of changed
        throw new UnsupportedOperationException("not yet implemented");
    }

    public Sample removeMetric(String metricName){
        //cannot call because sample is returned instead of changed
        throw new UnsupportedOperationException("not yet implemented");
//        removeMetricsWithPrefix(metricName);
    }

    public Sample removeMetricsWithPrefix(String prefix){
        //TODO do we need to take care of duplicate metrics?
        ArrayList<String> resultHeader = new ArrayList<String>();
        ArrayList<Double> resultMetrics = new ArrayList<>();
//        int count = 0;
        for (int i = 0; i < header.header.length ; i++){
            if(!header.header[i].startsWith(prefix)){
                resultHeader.add(header.header[i]);
                resultMetrics.add(metrics[i]);
//                resultMetricsIndexes.add(i);
//                count++;
            }
        }
        //TODO double check by anton wether this is valid usage of header field

        String[] headers = new String[resultHeader.size()];
        headers = resultHeader.toArray(headers);
        Header newHeader = new Header(headers);
        double[] newMetrics = new double[headers.length];
        Double[] boxHolder = new Double[newMetrics.length];
        newMetrics = ArrayUtils.toPrimitive(resultMetrics.toArray(boxHolder));
        //new double[count];

//        this is all dirty hacking...
//        for(int i = 0; i < this.metrics.length - count; i++){
//            newMetrics[i]
//        }
        Sample sampleToReturn = new Sample(newHeader, newMetrics, this);
        return sampleToReturn;
    }

    public void removeMetricsWithPrefix(String ... prefix){
        //cannot call because sample is returned instead of changed
        throw new UnsupportedOperationException("not yet implemented");
//        for (String s : prefix) removeMetricsWithPrefix(s);
    }

}
