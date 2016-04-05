package Marshaller;

import MetricIO.MetricsSample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller_Interface {

    public String[] unmarshallSampleHeader(DataInputStream header) throws IOException;
    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics, String[] header) throws IOException;

    public void marshallSampleHeader(String[] header, DataOutputStream outputStream);
    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream);
}
