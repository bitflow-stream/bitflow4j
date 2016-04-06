package Marshaller;

import Metrics.Sample;

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

    public String[] unmarshallHeader(InputStream input) throws IOException {
        return readLine(input).split(",");
    }

    public Sample unmarshallSample(InputStream input, String[] header) throws IOException {
        String[] metricsStrArr = readLine(input).split(",");
        if (metricsStrArr.length == 0) {
            throw new IOException("Received empty sample");
        }
        // TODO error if number of values is not equal to the number of header fields

        // generate timestamp from first value
        String timestampAsString = metricsStrArr[0];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date timestamp;
        try {
            if (timestampAsString.length() < 23) {
                throw new IOException("Failed to parse timestamp field");
            }
            timestamp = formatter.parse(timestampAsString.substring(0, 23));
        } catch (ParseException exc) {
            throw new IOException(exc);
        }

        Double[] metricsDblArr = new Double[metricsStrArr.length - 1];

        for (int i = 1; i < metricsStrArr.length; i++){
            try {
                metricsDblArr[i - 1] = Double.valueOf(metricsStrArr[i]);
            } catch (NumberFormatException exc) {
                throw new IOException(exc);
            }
        }

        return new Sample(header, timestamp, metricsDblArr);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public void marshallHeader(OutputStream output, String[] header) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

}
