package bitflow4j.io;

import bitflow4j.Header;
import bitflow4j.Marshaller;
import bitflow4j.Sample;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Reads Samples from a single InputStream instance until it is closed.
 */
public class MetricReader implements MetricInputStream {

    private static final Logger logger = Logger.getLogger(MetricReader.class.getName());

    private final Marshaller marshaller;
    private final InputStream input;
    public final String sourceName;
    private Header header = null;

    public MetricReader(InputStream input, String sourceName, Marshaller marshaller) {
        this.marshaller = marshaller;
        this.sourceName = sourceName;
        this.input = new BufferedInputStream(input);
    }

    public void setCurrentHeader(Header header) {
        this.header = header;
    }

    public Header currentHeader() {
        return header;
    }

    public Sample readSample() throws IOException {
        while (true) {
            try {
                boolean isHeader = marshaller.peekIsHeader(input);
                if (isHeader) {
                    header = marshaller.unmarshallHeader(input);
                    logger.info("Incoming header of '" + sourceName + "' updated to " + header.header.length + " metrics");
                } else {
                    if (header == null) {
                        throw new IOException("Input stream '" + sourceName + "' contains Sample before first Header");
                    }
                    Sample sample = marshaller.unmarshallSample(input, header);
                    if (!sample.hasSource())
                        sample.setSource(sourceName);
                    return sample;
                }
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

    public void close() throws IOException {
        input.close();
    }

}
