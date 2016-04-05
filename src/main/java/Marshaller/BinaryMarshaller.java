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
    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics, String[] header) throws IOException {

        MetricsSample sample = new MetricsSample();
        sample.setMetricsHeader(header);

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
    public String[] unmarshallSampleHeader(DataInputStream header) throws IOException {

        String value = "";
        List<String> headerList = new ArrayList<String>();

        while((value = header.readUTF()).isEmpty()) {

            headerList.add(value);
        }

        return (String[]) headerList.toArray();
    }

    @Override
    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream) {

    }

    @Override
    public void marshallSampleHeader(String[] header, DataOutputStream outputStream) {

    }
}

