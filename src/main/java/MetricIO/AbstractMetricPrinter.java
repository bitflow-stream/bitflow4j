package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

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
        if (output == null || headerChanged(header)) {
            output = nextOutputStream();
            marshaller.marshallHeader(output, header);
            lastHeader = header;
        }
        marshaller.marshallSample(output, sample);
    }

    private boolean headerChanged(String[] header) {
        if (header.length != lastHeader.length) {
            return true;
        } else if (header != lastHeader) {
            // New instance with same length: must compare all header fields
            for (int i = 0; i < header.length; i++) {
                if (!header[i].equals(lastHeader[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}
