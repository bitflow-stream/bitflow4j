package bitflow4j.io;

import bitflow4j.Marshaller;

import java.io.*;

/**
 * Simple MetricPrinter writing to a single fixed OutputStream.
 *
 * Created by anton on 4/7/16.
 */
public class MetricPrinter extends AbstractMetricPrinter {

    private final OutputStream output;

    public MetricPrinter(Marshaller marshaller) {
        this(System.out, marshaller);
    }

    public MetricPrinter(String filename, Marshaller marshaller) throws FileNotFoundException {
        this(new FileOutputStream(filename, false), marshaller);
    }

    public MetricPrinter(File file, Marshaller marshaller) throws FileNotFoundException {
        this(new FileOutputStream(file, false), marshaller);
    }

    public MetricPrinter(OutputStream output, Marshaller marshaller) {
        super(marshaller);
        this.output = output;
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        return output;
    }

}
