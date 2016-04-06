package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by anton on 4/6/16.
 */
public class MetricPrinter implements MetricOutputStream {

    private final OutputStream output;
    private final Marshaller marshaller;

    public MetricPrinter(Marshaller marshaller) {
        this(System.out, marshaller);
    }

    public MetricPrinter(String filename, Marshaller marshaller) throws FileNotFoundException {
        // TODO opened file is not closed
        this(new FileOutputStream(filename), marshaller);
    }

    public MetricPrinter(OutputStream output, Marshaller marshaller) {
        this.output = output;
        this.marshaller = marshaller;
    }

    public void writeSample(Sample sample) throws IOException {
        marshaller.marshallSample(output, sample);
    }

}
