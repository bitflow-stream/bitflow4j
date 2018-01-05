package bitflow4j.io.marshall;

import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.*;

/**
 * Created by alex on 18.12.17
 */
public class CsvMarshallerExt extends AbstractMarshaller {

    private static final String CSV_HEADER_TAGS = "tags";

    private String firstColName;
    private String separator;
    private byte[] separatorBytes;
    private Set<String> tagMetrics;

    private Map<String, Integer> mappingTagMetricIndex = new HashMap<>();


    public CsvMarshallerExt(String firstColName, String separator){
        this.firstColName = firstColName;
        this.separator = separator;
        this.separatorBytes = separator.getBytes();
    }

    public CsvMarshallerExt(String firstColName, String separator, Set<String> tagMetrics){
        this(firstColName, separator);
        this.tagMetrics = tagMetrics;
    }

    @Override
    public boolean peekIsHeader(InputStream input) throws IOException {
        //Handle files with byte order mark
        //TODO can only handle UTF-8. Other boms have different byte size. Adjust!
        byte peekedWithBom[] = peek(input, firstColName.length() + 3);
        if(Arrays.equals(Arrays.copyOfRange(peekedWithBom, 3, peekedWithBom.length), firstColName.getBytes())){
            for(int i = 0; i < 3; i++)
                input.read();
            return true;
        }
        //Handle files without byte order mark
        byte peeked[] = peek(input, firstColName.length());
        return Arrays.equals(peeked, firstColName.getBytes());
    }

    @Override
    public UnmarshalledHeader unmarshallHeader(InputStream input) throws IOException {
        String[] fields = readLine(input).split(separator, -1);

        if (fields.length < 1 || !fields[0].equals(firstColName)) {
            throw new IllegalArgumentException("First field in CSV header must be " + firstColName);
        }
        boolean hasTags = fields.length > 0 && fields[fields.length-1].equals(CSV_HEADER_TAGS);

        int specialFields = hasTags ? 1 : 0;
        if(tagMetrics != null) {
            this.mapTagMetricsToIndices(fields);
        }
        //Create header. Take only selected fields.
        String header[] = this.createHeader(fields, specialFields);
        return new UnmarshalledHeader(new Header(header), hasTags);
    }

    private void mapTagMetricsToIndices(String[] metricNames){
        for(int i = 0; i < metricNames.length; i++){
            if(tagMetrics.contains(metricNames[i]))
                mappingTagMetricIndex.put(metricNames[i], i);
        }
    }

    private String[] createHeader(String[] metricNames, int specialFields) {
        String header[] = new String[metricNames.length - mappingTagMetricIndex.size() - specialFields];
        for(int i = 0, j = 0; i < metricNames.length - specialFields; i++){
            if(!mappingTagMetricIndex.containsValue(i))
                header[j++] = metricNames[i];
        }
        return header;
    }

    @Override
    public Sample unmarshallSample(InputStream input, UnmarshalledHeader header) throws IOException {
        String sampleStr = readLine(input);
        String[] metricStrings = sampleStr.split(separator, -1);
        if (metricStrings.length < 1)
            throw new IOException("Illegal CSV Sample: " + sampleStr);

        String tags = null;
        double[] metricValues;

        // Parse special fields
        if (header.hasTags) {
            if (metricStrings.length < 1) {
                throw new IOException("Sample has no tags: " + sampleStr + " ==> " + Arrays.toString(metricStrings));
            }
            tags = metricStrings[metricStrings.length - 1];
        }

        // Parse regular values. Exclude values which should be set as tags
        int metricSize = metricStrings.length;
        int hasTags = 0;
        if (header.hasTags){
            metricSize--;
            hasTags = 1;
        }

        metricSize -= mappingTagMetricIndex.size();

        metricValues = new double[metricSize];
        for (int i = 0, j = 0; i < metricStrings.length - hasTags; i++) {
            if(!mappingTagMetricIndex.values().contains(i)) {
                try {
                    metricValues[j] = Double.valueOf(metricStrings[i]);
                } catch (NumberFormatException exc) {
                    metricValues[j] = tryAlternativeNumberParser(metricStrings[i]);
                }
                j++;
            }
        }

        Sample s = Sample.unmarshallSample(header.header, metricValues, new Date(), tags);
        s = this.addMetricTags(s, metricStrings);
        return s;
    }

    private double tryAlternativeNumberParser(String metricString) throws IOException{
        Double value;
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        try {
            Number n = df.parse(metricString);
            value = n.doubleValue();
        }catch (ParseException ex){
            throw new IOException(ex);
        }
        return value;
    }

    private Sample addMetricTags(Sample s, String[] metricStrings) {
        for(Map.Entry<String, Integer> entry : mappingTagMetricIndex.entrySet())
            s.setTag(entry.getKey(), metricStrings[entry.getValue()]);
        return s;
    }

    private void printString(OutputStream output, String string, boolean separate) throws IOException {
        if (separate) {
            output.write(separatorBytes);
        }
        output.write(string.getBytes());
    }

    private void printStrings(OutputStream output, String[] strings) throws IOException {
        for (int i = 0; i < strings.length; i++) {
            printString(output, strings[i], i > 0);
        }
    }

    @Override
    public void marshallHeader(OutputStream output, Header header) throws IOException {
        String[] completeHeader = rebuildHeaderFromTags(header.header);
        printStrings(output, completeHeader);
        output.write(separatorBytes);
        output.write(CSV_HEADER_TAGS.getBytes());
        output.write(lineSepBytes_1);
    }

    private String[] rebuildHeaderFromTags(String[] header) {
        String[] completeHeader = new String[header.length + mappingTagMetricIndex.size()];
        for(int i = 0, j = 0; i < completeHeader.length; i++){
            if(!mappingTagMetricIndex.values().contains(i)){
                completeHeader[i] = header[j++];
            }
        }
        for(Map.Entry<String, Integer> entry : mappingTagMetricIndex.entrySet()){
            completeHeader[entry.getValue()] = entry.getKey();
        }
        return completeHeader;
    }

    @Override
    public void marshallSample(OutputStream output, Sample sample) throws IOException {

        boolean separate = false;
        String[] completeValues = rebuildValuesFromTags(sample.getMetrics(), sample.getTags());
        for (String value : completeValues) {
            printString(output, value, separate);
            separate = true;
        }

        // Write tags
        output.write(separatorBytes);

        this.removeMetricTags(sample);
        output.write(sample.tagString().getBytes());

        output.write(lineSepBytes_1);
    }

    private void removeMetricTags(Sample sample) {
        for(String tagMetric : tagMetrics)
            sample.getTags().remove(tagMetric);
    }

    private String[] rebuildValuesFromTags(double[] values, Map<String, String> tags) {
        String[] completeValues = new String[values.length + mappingTagMetricIndex.size()];
        for(int i = 0, j = 0; i < completeValues.length; i++){
            if(!mappingTagMetricIndex.values().contains(i)){
                completeValues[i] = String.valueOf(values[j++]);
            }
        }
        for(Map.Entry<String, String> tag : tags.entrySet()){
            if(mappingTagMetricIndex.containsKey(tag.getKey())){
                completeValues[mappingTagMetricIndex.get(tag.getKey())] = tag.getValue();
            }
        }
        return completeValues;
    }

}
