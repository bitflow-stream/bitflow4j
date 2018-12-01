package bitflow4j.io.marshall;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 * <p>
 * This Marshaller formats Samples in a human readable format. Intended for console-output.
 */
public class TextMarshaller extends AbstractMarshaller implements Marshaller {

    private static final byte[] METRICS_SEPARATOR_BYTES = ", ".getBytes();
    private static final String equalsStr = "=";
    private static final byte[] COLON_BYTES = ": ".getBytes();
    private static final byte[] OPEN_BYTES = " (".getBytes();
    private static final byte[] CLOSE_BYTES = ")".getBytes();

    public static final String FORMAT = "TEXT";

    @Override
    public String toString() {
        return FORMAT;
    }

    public boolean peekIsHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public UnmarshalledHeader unmarshallHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public Sample unmarshallSample(InputStream input, UnmarshalledHeader header) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        // empty
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        output.write(sample.getTimestamp().toString().getBytes());
        if (sample.hasTags()) {
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
            String outStr = header[i] + equalsStr + values[i];
            output.write(outStr.getBytes());
        }
        output.write(lineSepBytes_1);
    }

}
