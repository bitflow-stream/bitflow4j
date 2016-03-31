package Marshaller;

import MetricIO.MetricsSample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller_Interface {

    public MetricsSample unmarshallSampleMetrics(DataInputStream metrics) throws IOException, ParseException;
    public MetricsSample unmarshallSampleHeader(DataInputStream header) throws IOException;

    public void marshallSampleMetrics(MetricsSample metricsSample, DataOutputStream outputStream);
    public void marshallSampleHeaders(MetricsSample metricsSample, DataOutputStream outputStream);
}

