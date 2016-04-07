package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mwall on 30.03.16.
 *
 * Reads metrics from a file
 */
public class FileMetricInputStream implements MetricInputStream {

    private InputStream input;
    private String[] header;

    private Marshaller marshaller = null;

    public FileMetricInputStream(String FilePath, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        input = new FileInputStream(FilePath);
        this.header = marshaller.unmarshallHeader(input);
    }

    public Sample readSample() throws IOException {
        return this.marshaller.unmarshallSample(input, header);
    }

}
