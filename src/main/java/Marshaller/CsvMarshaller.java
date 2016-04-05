package Marshaller;

import Metrics.Sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
public class CsvMarshaller implements Marshaller {

    private static int lineSep = System.getProperty("line.separator").charAt(0);

    public String[] unmarshallHeader(DataInputStream input) throws IOException {

        int chr;
        StringBuffer buffer = new StringBuffer(20);

        while ((chr = input.read()) != lineSep) {
            buffer.append((char) chr);
        }

        return buffer.toString().split(",");
    }

    public Sample unmarshallSample(DataInputStream input, String[] header) throws IOException {

        int chr;
        StringBuffer buffer = new StringBuffer(512);

        while ((chr = input.read()) != lineSep) {
            buffer.append((char) chr);
        }
        String[] metricsStrArr = buffer.toString().split(",");
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

    public void marshallSample(DataOutputStream output, Sample sample) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public void marshallHeader(DataOutputStream output, String[] header) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

}
