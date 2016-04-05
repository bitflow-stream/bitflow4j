package MetricIO;

import Marshaller.Marshaller_Interface;

/**
 *
 * @author fschmidt
 */
public class FileMetricOutputStream implements MetricOutputStream{

    private final Marshaller_Interface marshaller;
    private final String filePath;

    public FileMetricOutputStream(String filePath, Marshaller_Interface marshaller){
        this.filePath = filePath;
        this.marshaller = marshaller;
    }
    
    @Override
    public void writeSample(MetricsSample data) {
        // TODO check if this is the first write, or if the header has changed since the last
        // writeSample() call. If so, open a new file and write the header.
        // Then, write the sample.

        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
