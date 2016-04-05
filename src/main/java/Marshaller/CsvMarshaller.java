package Marshaller;

import MetricIO.MetricsSample;

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
public class CsvMarshaller implements Marshaller_Interface{

    static int lineSep = System.getProperty("line.separator").charAt(0);

    @Override
    public String[] unmarshallSampleHeader(DataInputStream header) throws IOException {

        int chr;
        StringBuffer desc = new StringBuffer(20);

        while ((chr = header.read()) != lineSep) {
            desc.append((char) chr);
        }

        return desc.toString().split(",");
    }

    @Override
    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics, String[] header) throws IOException {

        MetricsSample sample = new MetricsSample();
        sample.setMetricsHeader(header);

        int chr;
        StringBuffer desc = new StringBuffer(20);

        while ((chr = metrics.read()) != lineSep) {
            desc.append((char) chr);
        }
        String[] metricsStrArr = desc.toString().split(",");
        // generate timestamp from first value
        String timestampAsString = metricsStrArr[0];
        String timestampSubstring = timestampAsString.substring(0, 23);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        Date timestamp;
        try {
            timestamp = formatter.parse(timestampSubstring);
        } catch (ParseException exc) {
            throw new IOException(exc);
        }

        Double[] metricsDblArr = new Double[metricsStrArr.length - 1];

        for (int i = 1; i < metricsStrArr.length; i++){

            metricsDblArr[i-1] = Double.valueOf(metricsStrArr[i]);

        }

        sample.setTimestamp(timestamp);
        sample.setMetrics(metricsDblArr);
        return sample;
    }


    @Override
    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream) {

    }

    @Override
    public void marshallSampleHeader(String[] header, DataOutputStream outputStream) {

    }
}
