package MetricIO;

import Marshaller.Marshaller_Interface;

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

    private Marshaller_Interface marshaller = null;

    public FileMetricInputStream(String FilePath, Marshaller_Interface marshaller) throws IOException {
        this.marshaller = marshaller;

        FileInputStream fileStream = new FileInputStream(FilePath);
        data = new DataInputStream(fileStream);

        this.header = marshaller.unmarshallSampleHeader(data);
    }

    public MetricsSample readSample() throws IOException {

        return this.marshaller.unmarshallSampleMetrics(this.data, this.header);

    }

}
