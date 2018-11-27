package bitflow4j.io;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.io.marshall.Marshaller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements PipelineStep using an instance Marshaller to marshall Sample instances
 * into a byte-oriented OutputStream like a file or network connection.
 * <p>
 * Created by anton on 4/6/16.
 */
public abstract class MarshallingSampleWriter extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(MarshallingSampleWriter.class.getName());

    private final Marshaller marshaller;
    protected OutputStream output = null;
    private final Header.ChangeChecker header = new Header.ChangeChecker();

    public MarshallingSampleWriter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public Marshaller getMarshaller() {
        return marshaller;
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        OutputStream output = this.output; // Avoid race condition
        try {
            if (output == null || header.changed(sample.getHeader())) {
                OutputStream newOutput = nextOutputStream();

                // Use object identity to determine if the output stream has changed. If so, close the old one.
                if (newOutput != output) {
                    closeStream();
                    this.output = newOutput;
                    output = newOutput;
                }
                if (output == null)
                    return;
                marshaller.marshallHeader(output, sample.getHeader());
                output.flush();
            }
            marshaller.marshallSample(output, sample);
        } catch (IOException e) {
            closeStream();
            throw e;
        }
        super.writeSample(sample);
    }

    protected void closeStream() {
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

    @Override
    public void doClose() {
        closeStream();
    }

}
