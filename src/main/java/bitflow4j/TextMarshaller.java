package bitflow4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 * <p>
 * This Marshaller only Samples in a well-readable format. Intended for console-output.
 */
public class TextMarshaller extends AbstractMarshaller implements Marshaller {

    private static final byte[] METRICS_SEPARATOR_BYTES = ", ".getBytes();
    private static final String equalsStr = "=";
    private static final byte[] COLON_BYTES = ": ".getBytes();
    private static final byte[] OPEN_BYTES = " (".getBytes();
    private static final byte[] CLOSE_BYTES = ")".getBytes();

    public Header unmarshallHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public Sample unmarshallSample(InputStream input, Header header) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        // empty
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        sample.checkConsistency();

        output.write(sample.getTimestamp().toString().getBytes());
        if (sample.getHeader().hasTags) {
            output.write(OPEN_BYTES);
            output.write(sample.tagString().getBytes());
            output.write(CLOSE_BYTES);
        }
        output.write(COLON_BYTES);

        String[] header = sample.getHeader().header;
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            if (i > 0) {
                output.write(METRICS_SEPARATOR_BYTES);
            }
            String outStr = header[i] + equalsStr + String.valueOf(values[i]);
            output.write(outStr.getBytes());
        }
        output.write(lineSepBytes);
    }

}
