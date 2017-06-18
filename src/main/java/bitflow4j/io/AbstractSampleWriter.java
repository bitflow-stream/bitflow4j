package bitflow4j.io;

import bitflow4j.io.marshall.Marshaller;
import bitflow4j.sample.AbstractSink;
import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements Sink using an instance Marshaller to marshall Sample instances
 * into a byte-oriented OutputStream like a file or network connection.
 * <p>
 * Created by anton on 4/6/16.
 */
public abstract class AbstractSampleWriter extends AbstractSink {

    private static final Logger logger = Logger.getLogger(AbstractSampleWriter.class.getName());

    private final Marshaller marshaller;
    protected OutputStream output = null;
    private Header lastHeader;
    private final boolean extendFile;

    public AbstractSampleWriter(Marshaller marshaller) {
        this(marshaller, false);
    }
    
    public AbstractSampleWriter(Marshaller marshaller, boolean extendFile) {
        this.marshaller = marshaller;
        this.extendFile = extendFile;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public synchronized void writeSample(Sample sample) throws IOException {
        Header header = sample.getHeader();
        OutputStream output = this.output; // Avoid race condition
        try {
            if ((output == null || sample.headerChanged(lastHeader)) && !extendFile) {
                closeStream();
                this.output = nextOutputStream();
                output = this.output;
                if (output == null)
                    return;
                marshaller.marshallHeader(output, header);
                lastHeader = header;
            } else if((output == null || sample.headerChanged(lastHeader)) && extendFile){
                if (output == null)
                    return;
                marshaller.marshallHeader(output, header);
                lastHeader = header;
            }
            marshaller.marshallSample(output, sample);
        } catch (IOException e) {
            closeStream();
            throw e;
        }
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

    public void close() {
        closeStream();
        super.close();
    }

}
