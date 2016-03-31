package Marshaller;

import MetricIO.MetricsSample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 */
public class BinaryMarshaller implements Marshaller_Interface {

    @Override
    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics) throws IOException {

        MetricsSample sample = new MetricsSample();

        Date timestamp = new Date(metrics.readLong() / 1000000);
        Double value= 0.0;
        List<Double> metricList = new ArrayList<Double>();

        while((value = metrics.readDouble()) != null) {
            metricList.add(value);
        }

        sample.setTimestamp(timestamp);
        sample.setMetrics((Double[]) metricList.toArray());

        return sample;
    }

    @Override
    public MetricsSample unmarshallSampleHeader(DataInputStream header) throws IOException {

        MetricsSample sample = new MetricsSample();
        String value = "";
        List<String> headerList = new ArrayList<String>();

        while((value = header.readUTF()).isEmpty()) {
            headerList.add(value);
        }

        sample.setMetricsHeader((String[]) headerList.toArray());
            return sample;
    }

    @Override
    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream) {

    }

    @Override
    public void marshallSampleHeaders(MetricsSample metricsSample, DataOutputStream outputStream) {

    }
}

