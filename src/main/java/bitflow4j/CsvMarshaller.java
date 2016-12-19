package bitflow4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Sample marshaller for CSV format.
 */
public class CsvMarshaller extends AbstractMarshaller {

    private static final String CSV_HEADER_TIME = "time";
    private static final String CSV_HEADER_TAGS = "tags";
    private static final String separator = ",";
    private static final byte[] separatorBytes = separator.getBytes();

    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final int dateLength = dateFormat.length();
    public static final SimpleDateFormat date_formatter = new SimpleDateFormat(dateFormat);

    public boolean peekIsHeader(BufferedInputStream input) throws IOException {
        byte peeked[] = peek(input, CSV_HEADER_TIME.length());
        return Arrays.equals(peeked, CSV_HEADER_TIME.getBytes());
    }

    public Header unmarshallHeader(InputStream input) throws IOException {
        String[] fields = readLine(input).split(separator);

        if (fields.length < 1 || !fields[0].equals(CSV_HEADER_TIME)) {
            throw new IllegalArgumentException("First field in CSV header must be " + CSV_HEADER_TIME);
        }
        boolean hasTags = fields.length >= 2 && fields[1].equals(CSV_HEADER_TAGS);

        int specialFields = hasTags ? 2 : 1;
        String header[] = new String[fields.length - specialFields];
        System.arraycopy(fields, specialFields, header, 0, header.length);
        return new Header(header, hasTags);
    }

    public Sample unmarshallSample(InputStream input, Header header) throws IOException {
        String sampleStr = readLine(input);
        String[] metricStrings = sampleStr.split(separator);
        if (metricStrings.length < 1)
            throw new IOException("Illegal CSV Sample: " + sampleStr);

        Date timestamp;
        String tags = null;
        double[] metricValues;

        // Parse special fields
        String timestampAsString = metricStrings[0];
        try {
            if (timestampAsString.length() < dateLength) {
                throw new IOException("Failed to parse timestamp field");
            }
            timestamp = date_formatter.parse(timestampAsString.substring(0, dateLength));
        } catch (ParseException exc) {
            throw new IOException(exc);
        }
        if (header.hasTags) {
            if (metricStrings.length < 2) {
                throw new IOException("Sample has no tags: " + sampleStr);
            }
            tags = metricStrings[1];
        }

        // Parse regular values
        int start = 1;
        if (header.hasTags) start++;
        metricValues = new double[metricStrings.length - start];
        for (int i = start; i < metricStrings.length; i++) {
            try {
                metricValues[i - start] = Double.valueOf(metricStrings[i]);
            } catch (NumberFormatException exc) {
                throw new IOException(exc);
            }
        }

        return Sample.unmarshallSample(header, metricValues, timestamp, tags);
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

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        output.write(CSV_HEADER_TIME.getBytes());
        if (header.hasTags) {
            output.write(lineSepBytes);
            output.write(CSV_HEADER_TAGS.getBytes());
        }

        if (header.header.length > 0)
            output.write(separatorBytes);
        printStrings(output, header.header);
        output.write(lineSepBytes);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        Header header = sample.getHeader();
        String dateStr = date_formatter.format(sample.getTimestamp());
        output.write(dateStr.getBytes());
        if (header.hasTags) {
            output.write(separatorBytes);
            output.write(sample.tagString().getBytes());
        }

        double[] values = sample.getMetrics();
        for (double value : values) {
            printString(output, String.valueOf(value), true);
        }
        output.write(lineSepBytes);
    }

}
