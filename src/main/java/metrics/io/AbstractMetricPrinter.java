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
    private String[] lastHeader;

    public AbstractMetricPrinter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract OutputStream nextOutputStream() throws IOException;

    public synchronized void writeSample(Sample sample) throws IOException {
        String[] header = sample.getHeader();
        if (header.length == 0) {
            return;
        }
        if (output == null || sample.headerChanged(lastHeader)) {
            output = nextOutputStream();
            marshaller.marshallHeader(output, header);
            lastHeader = header;
        }
        marshaller.marshallSample(output, sample);
    }

}
