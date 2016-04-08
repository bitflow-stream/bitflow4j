package metrics.io;

import metrics.Marshaller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
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

    public MetricPrinter(OutputStream output, Marshaller marshaller) {
        super(marshaller);
        this.output = output;
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        return output;
    }

}
