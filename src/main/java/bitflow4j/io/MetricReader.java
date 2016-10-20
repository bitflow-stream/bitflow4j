package bitflow4j.io;

import bitflow4j.Header;
import bitflow4j.Marshaller;
import bitflow4j.Sample;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Reads Samples from a single InputStream instance until it is closed.
 */
public class MetricReader implements MetricInputStream {

    private final Marshaller marshaller;
    private final InputStream input;
    private final String sourceName;
    private Header header;

    public MetricReader(InputStream input, String sourceName, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        this.input = input;
        this.sourceName = sourceName;
        header = marshaller.unmarshallHeader(input);
    }

    public Sample readSample() throws IOException {
        try {
            Sample sample = marshaller.unmarshallSample(input, header);
            if (!sample.hasSource())
                sample.setSource(sourceName);
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
