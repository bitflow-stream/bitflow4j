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
 * parses csv like header, metrics to a Sample object containing String[], and float[]
 * and further cuts of the first metric and sets it as timestamp in sample object
 *
 */
public class CsvMarshaller extends AbstractMarshaller {

    static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    static final int dateLength = dateFormat.length();
    static final String separator = ",";
    static final byte[] separatorBytes = separator.getBytes();

    public String[] unmarshallHeader(InputStream input) throws IOException {
        return readLine(input).split(separator);
    }

    public Sample unmarshallSample(InputStream input, String[] header) throws IOException {
        String[] metricsStrArr = readLine(input).split(separator);
        if (metricsStrArr.length == 0) {
            throw new IOException("Received empty sample");
        }
        // TODO error if number of values is not equal to the number of header fields

        // generate timestamp from first value
        String timestampAsString = metricsStrArr[0];
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date timestamp;
        try {
            if (timestampAsString.length() < dateLength) {
                throw new IOException("Failed to parse timestamp field");
            }
            timestamp = formatter.parse(timestampAsString.substring(0, dateLength));
        } catch (ParseException exc) {
            throw new IOException(exc);
        }

        double[] metricsDblArr = new double[metricsStrArr.length - 1];

        for (int i = 1; i < metricsStrArr.length; i++){
            try {
                metricsDblArr[i - 1] = Double.valueOf(metricsStrArr[i]);
            } catch (NumberFormatException exc) {
                throw new IOException(exc);
            }
        }

        return new Sample(header, timestamp, metricsDblArr);
    }

    public void marshallHeader(OutputStream output, String[] header) throws IOException {
        for (int i = 0; i < header.length; i++) {
            if (i > 0) {
                output.write(separatorBytes);
            }
            output.write(header[i].getBytes());
        }
        output.write(lineSepBytes);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        double[] values = sample.getMetrics();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                output.write(separatorBytes);
            }
            output.write(String.valueOf(values[i]).getBytes());
        }
        output.write(lineSepBytes);
    }

}
