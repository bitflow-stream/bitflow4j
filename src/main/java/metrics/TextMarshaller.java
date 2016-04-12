package metrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 */
public class TextMarshaller extends AbstractMarshaller implements Marshaller {

    private static final byte[] METRICS_SEPARATOR_BYTES = ", ".getBytes();
    private static final String equalsStr = "=";
    public static final byte[] COMMA_BYTES = ", ".getBytes();
    public static final byte[] SOURCE_BYTES = "source: ".getBytes();
    public static final byte[] COLON_BYTES = ": ".getBytes();
    public static final byte[] LABEL_BYTES = "label: ".getBytes();

    public Sample.Header unmarshallHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public Sample unmarshallSample(InputStream input, Sample.Header unmarshallingHeader,
                                   Sample.Header sampleHeader) throws IOException {
        throw new UnsupportedOperationException("TextMarshaller does not support unmarshalling");
    }

    public void marshallHeader(OutputStream output, Sample.Header header) throws IOException {
        // empty
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        sample.checkConsistency();

        boolean specialFieldWritten = false;
        if (sample.hasTimestamp()) {
            output.write(sample.getTimestamp().toString().getBytes());
            specialFieldWritten = true;
        }
        if (sample.hasSource()) {
            if (specialFieldWritten)
                output.write(COMMA_BYTES);
            output.write(SOURCE_BYTES);
            output.write(sample.getSource().getBytes());
            specialFieldWritten = true;
        }
        if (sample.hasLabel()) {
            if (specialFieldWritten)
                output.write(COMMA_BYTES);
            output.write(LABEL_BYTES);
            output.write(sample.getLabel().getBytes());
            specialFieldWritten = true;
        }
        if (specialFieldWritten)
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
