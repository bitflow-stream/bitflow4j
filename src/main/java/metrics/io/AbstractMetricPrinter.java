package metrics.io;

import metrics.Header;
import metrics.Marshaller;
import metrics.Sample;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 */
public abstract class AbstractMetricPrinter extends AbstractOutputStream {

    private final Marshaller marshaller;
    protected OutputStream output = null;
    private Header lastHeader;

    public AbstractMetricPrinter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public synchronized void writeSample(Sample sample) throws IOException {
        Header header = sample.getHeader();
        if (header.numFields() <= 0) {
            return;
        }
        OutputStream output = this.output; // Avoid race condition
        if (output == null || sample.headerChanged(lastHeader)) {
            this.output = nextOutputStream();
            output = this.output;
            marshaller.marshallHeader(output, header);
            lastHeader = header;
        }
        marshaller.marshallSample(output, sample);
    }

    public void close() throws IOException {
        OutputStream output = this.output; // Avoid race condition
        if (output != null) {
            this.output = null;
            output.close();
        }
        super.close();
    }

}
