package Marshaller;

import MetricIO.MetricsSample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller {

    String[] unmarshallHeader(DataInputStream input) throws IOException;
    MetricsSample unmarshallSample(DataInputStream input, String[] header) throws IOException;

    void marshallHeader(DataOutputStream output, String[] header) throws IOException;
    void marshallSample(DataOutputStream output, MetricsSample sample) throws IOException;

}
