package metrics.io;

import metrics.Marshaller;
import metrics.Sample;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 */
public abstract class AbstractMetricPrinter implements MetricOutputStream {

    private final Marshaller marshaller;
    private OutputStream output = null;
    private Sample.Header lastHeader;

    public AbstractMetricPrinter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public synchronized void writeSample(Sample sample) throws IOException {
        Sample.Header header = sample.getHeader();
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
            output.close();
            this.output = null;
        }
    }

}
