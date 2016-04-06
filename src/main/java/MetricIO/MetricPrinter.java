package MetricIO;

import Marshaller.Marshaller;

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

    public MetricPrinter(OutputStream output, Marshaller marshaller) {
        super(marshaller);
        this.output = output;
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        return output;
    }

}
