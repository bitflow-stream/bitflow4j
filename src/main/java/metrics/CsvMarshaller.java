package metrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 *
 * parses csv like header, metrics to a Sample object containing String[], and double[]
 * and further cuts of the first metric and sets it as timestamp in sample object
 */
public class CsvMarshaller extends AbstractMarshaller {

    private static final String separator = ",";
    private static final byte[] separatorBytes = separator.getBytes();

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final int dateLength = dateFormat.length();
    private static final SimpleDateFormat date_formatter = new SimpleDateFormat(dateFormat);

    public Sample.Header unmarshallHeader(InputStream input) throws IOException {
        String[] fields = readLine(input).split(separator);
        return new Sample.Header(fields);
    }

    public Sample unmarshallSample(InputStream input, Sample.Header header,
                                   Sample.Header sampleHeader) throws IOException {
        String[] metricStrings = readLine(input).split(separator);
        Date timestamp = null;
        String source = null;
        String label = null;
        double[] metricValues = new double[0];

        // Parse special fields
        if (header.hasTimestamp() && metricStrings.length >= Sample.Header.HEADER_TIME_IDX + 1) {
            String timestampAsString = metricStrings[Sample.Header.HEADER_TIME_IDX];
            try {
                if (timestampAsString.length() < dateLength) {
                    throw new IOException("Failed to parse timestamp field");
                }
                timestamp = date_formatter.parse(timestampAsString.substring(0, dateLength));
            } catch (ParseException exc) {
                throw new IOException(exc);
            }
        }
        if (header.hasSource() && metricStrings.length >= Sample.Header.HEADER_SOURCE_IDX + 1) {
            source = metricStrings[Sample.Header.HEADER_SOURCE_IDX];
        }
        if (header.hasLabel() && metricStrings.length >= Sample.Header.HEADER_LABEL_IDX + 1) {
            label = metricStrings[Sample.Header.HEADER_LABEL_IDX];
        }

        if (metricStrings.length > header.specialFields) {
            // Parse regular values
            metricValues = new double[metricStrings.length - header.specialFields];
            for (int i = header.specialFields; i < metricStrings.length; i++) {
                try {
                    metricValues[i - 1] = Double.valueOf(metricStrings[i]);
                } catch (NumberFormatException exc) {
                    throw new IOException(exc);
                }
            }
        }

        return new Sample(sampleHeader, metricValues, timestamp, source, label);
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

    public void marshallHeader(OutputStream output, Sample.Header header) throws IOException {
        String[] special = header.getSpecialFields();
        if (special.length > 0) {
            printStrings(output, special);
            if (header.header.length > 0)
                output.write(separatorBytes);
        }
        printStrings(output, header.header);
        output.write(lineSepBytes);
    }

    private void printSpecialField(OutputStream output, Object special, boolean separate) throws IOException {
        if (separate)
            output.write(separatorBytes);
        if (special != null) {
            output.write(special.toString().getBytes());
        }
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        Sample.Header header = sample.getHeader();
        boolean startedPrinting = false;
        if (header.hasTimestamp()) {
            printSpecialField(output, sample.getTimestamp(), startedPrinting);
            startedPrinting = true;
        }
        if (header.hasSource()) {
            printSpecialField(output, sample.getSource(), startedPrinting);
            startedPrinting = true;
        }
        if (header.hasLabel()) {
            printSpecialField(output, sample.getLabel(), startedPrinting);
            startedPrinting = true;
        }

        double[] values = sample.getMetrics();
        for (int i = 0; i < values.length; i++) {
            printString(output, String.valueOf(values[i]), startedPrinting);
            startedPrinting = true;
        }
        output.write(lineSepBytes);
    }

}
