package metrics.io;

import metrics.Marshaller;
import metrics.Sample;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 */
public class MetricReader implements MetricInputStream {

    private final Marshaller marshaller;
    private final InputStream input;
    private final String sourceName;
    private Sample.Header unmarshallingHeader;
    private Sample.Header header;

    public MetricReader(InputStream input, String sourceName, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        this.input = input;
        unmarshallingHeader = marshaller.unmarshallHeader(input);
        if (unmarshallingHeader.hasSource()) {
            System.err.println("Warning: Received header with source-field, ignoring configured" +
                    " source field: " + sourceName);
            this.sourceName = null;
            header = unmarshallingHeader;
        } else {
            this.sourceName = sourceName;
            header = new Sample.Header(unmarshallingHeader.header, Sample.Header.HEADER_SOURCE_IDX + 1);
        }
    }

    public Sample readSample() throws IOException {
        try {
            Sample sample = marshaller.unmarshallSample(input, unmarshallingHeader, header);
            if (sourceName != null) {
                sample.setSource(sourceName);
            }
            return sample;
        } catch (InputStreamClosedException exc) {
            try {
                input.close();
            } catch (IOException e) {
                // Ignore, we don't know if the stream was already closed.
            }
            throw exc;
        }
    }

}
