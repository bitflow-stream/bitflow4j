package MetricIO;

import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller_Interface;

/**
 *
 * @author fschmidt
 */
public class FileMetricOutputStream implements MetricOutputStream{

    public enum Format{
        BIN, CSV;
    }

    private final Format outputFormat;
    private final Marshaller_Interface marshaller;
    private final String filePath = "";
    
    public FileMetricOutputStream(Format outputFormat){
        this(outputFormat, "");
    }
    
    public FileMetricOutputStream(Format outputFormat, String filePath){
        this.outputFormat = outputFormat;
        switch (outputFormat) {
            case CSV:
                this.marshaller = new CsvMarshaller();
                break;
            case BIN:
                this.marshaller = new BinaryMarshaller();
                break;
            default:
                this.marshaller = new BinaryMarshaller();
                break;
        }
    }
    
    @Override
    public void writeSample(MetricsSample data) {
        this.marshaller.unmarshallSample(data);
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
