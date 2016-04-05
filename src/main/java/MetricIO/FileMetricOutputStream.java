package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public class FileMetricOutputStream implements MetricOutputStream{

    private final Marshaller marshaller;
    private final String filePath;

    public FileMetricOutputStream(String filePath, Marshaller marshaller){
        this.filePath = filePath;
        this.marshaller = marshaller;
    }
    
    @Override
    public void writeSample(Sample data) throws IOException {
        // TODO check if this is the first write, or if the header has changed since the last
        // writeSample() call. If so, open a new file and write the header.
        // Then, write the sample.

        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
