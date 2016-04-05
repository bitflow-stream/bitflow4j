package Marshaller;

import Metrics.Sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller {

    String[] unmarshallHeader(DataInputStream input) throws IOException;
    Sample unmarshallSample(DataInputStream input, String[] header) throws IOException;

    void marshallHeader(DataOutputStream output, String[] header) throws IOException;
    void marshallSample(DataOutputStream output, Sample sample) throws IOException;

}
