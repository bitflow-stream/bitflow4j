package metrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 */
public class TextMarshaller extends AbstractMarshaller implements Marshaller {

    private static final byte[] metricsSeparator = " ".getBytes();
    private static final String equals = "=";

    public String[] unmarshallHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public Sample unmarshallSample(InputStream input, String[] header) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public void marshallHeader(OutputStream output, String[] header) throws IOException {
        // empty
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        double[] values = sample.getMetrics();
        String[] header = sample.getHeader();
        if (values.length != header.length) {
            throw new IOException("Received sample size " + values.length +
                    ", but header is size " + header.length);
        }
        for (int i = 0; i < header.length; i++) {
            if (i > 0) {
                output.write(metricsSeparator);
            }
            String outStr = header[i] + equals + String.valueOf(values[i]);
            output.write(outStr.getBytes());
        }
        output.write(lineSepBytes);
    }

}
