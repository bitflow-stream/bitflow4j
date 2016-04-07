package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 */
public class MetricReader implements MetricInputStream {

    private final Marshaller marshaller;
    private final InputStream input;
    private String[] header;

    public MetricReader(InputStream input, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        this.input = input;
        header = marshaller.unmarshallHeader(input);
    }

    public Sample readSample() throws IOException {
        return marshaller.unmarshallSample(input, header);
    }

}