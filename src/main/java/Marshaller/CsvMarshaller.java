package Marshaller;

import MetricIO.MetricsSample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 *
 * parses csv like header, metrics to a Sample object containing String[], and float[]
 * and further cuts of the first metric and sets it as timestamp in sample object
 *
 */
public class CsvMarshaller implements Marshaller_Interface{


    @Override
    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics) throws IOException, ParseException {

        MetricsSample sample = new MetricsSample();

        String line = metrics.readUTF();

        String[] metricsStrArr = line.split(",");

        // generate timestamp from first value
        String timestampAsString = metricsStrArr[0];
        String timestampSubstring = timestampAsString.substring(0, 23);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        Date timestamp = formatter.parse(timestampSubstring);

        Double[] metricsDblArr = new Double[metricsStrArr.length-1];
        for (int i = 1; i < metricsStrArr.length; i++){
            metricsDblArr[i-1] = Double.valueOf(metricsStrArr[i]);
        }

        sample.setTimestamp(timestamp);
        sample.setMetrics(metricsDblArr);
        return sample;
    }

    @Override
    public MetricsSample unmarshallSampleHeader(DataInputStream header) throws IOException {
        System.out.println("header");

        String line = header.readUTF();
        System.out.println(line);
        String[] headerStrArr = line.split(",");

        MetricsSample sample = new MetricsSample();

        sample.setMetricsHeader(headerStrArr);

        return sample;
    }

    @Override
    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream) {

    }

    @Override
    public void marshallSampleHeaders(MetricsSample metricsSample, DataOutputStream outputStream) {

    }
}
