package MetricIO;

import Marshaller.Marshaller;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 *
 * Reads metrics from a file
 */
public class FileMetricInputStream implements MetricInputStream {

    private DataInputStream data;
    private String[] header;

    private Marshaller marshaller = null;

    public FileMetricInputStream(String FilePath, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;

        FileInputStream fileStream = new FileInputStream(FilePath);
        data = new DataInputStream(fileStream);

        this.header = marshaller.unmarshallHeader(data);
    }

    public MetricsSample readSample() throws IOException {
        return this.marshaller.unmarshallSample(this.data, this.header);
    }

}
