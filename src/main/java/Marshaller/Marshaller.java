package Marshaller;

import Metrics.Sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller {

    String[] unmarshallHeader(InputStream input) throws IOException;
    Sample unmarshallSample(InputStream input, String[] header) throws IOException;

    void marshallHeader(OutputStream output, String[] header) throws IOException;
    void marshallSample(OutputStream output, Sample sample) throws IOException;

}
