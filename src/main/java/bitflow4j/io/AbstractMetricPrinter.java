package bitflow4j.io;

import bitflow4j.Header;
import bitflow4j.Marshaller;
import bitflow4j.Sample;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements MetricOutputStream using an instance Marshaller to marshall Sample instances
 * into a byte-oriented OutputStream like a file or network connection.
 * <p>
 * Created by anton on 4/6/16.
 */
public abstract class AbstractMetricPrinter extends AbstractOutputStream {

    private static final Logger logger = Logger.getLogger(AbstractMetricPrinter.class.getName());

    private final Marshaller marshaller;
    protected OutputStream output = null;
    private Header lastHeader;

    public AbstractMetricPrinter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public synchronized void writeSample(Sample sample) throws IOException {
        Header header = sample.getHeader();
        OutputStream output = this.output; // Avoid race condition
        try {
            if (output == null || sample.headerChanged(lastHeader)) {
                closeStream();
                this.output = nextOutputStream();
                output = this.output;
                marshaller.marshallHeader(output, header);
                lastHeader = header;
            }
            marshaller.marshallSample(output, sample);
        } catch (IOException e) {
            closeStream();
            throw e;
        }
    }

    protected void closeStream() throws IOException {
        OutputStream output = this.output; // Avoid race condition
        if (output != null) {
            this.output = null;
            try {
                output.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to close output stream", e);
            }
        }
    }

    public void close() throws IOException {
        closeStream();
        super.close();
    }

}
