package MetricIO;

import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller_Interface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 *
 * Reads metrics from a file
 */
public class FileMetricInputStream implements MetricInputStream {

    private BufferedReader reader = null;
    private String metricsHeaderStr = "";
    private String format = "CSV";

    private Marshaller_Interface marshaller = null;

    public FileMetricInputStream(String FilePath) {
        this(FilePath,"CSV");
    }


    public FileMetricInputStream(String FilePath,String format) {
        this.format = format;
        switch (format) {
            case "CSV":
                this.marshaller = new CsvMarshaller();
                break;
            case "BIN":
                this.marshaller = new BinaryMarshaller();
                break;
            case "TEXT":
                //this.marshaller = new TextMarshaller();
                break;
        }

        try {
            reader = new BufferedReader(new FileReader(FilePath));

            this.metricsHeaderStr = "";
            // read header
            if (format == "BIN"){
                // as long as empty
                String tmpHeader = "";
                while(!(tmpHeader = reader.readLine()).isEmpty()){
                    this.metricsHeaderStr += tmpHeader+",";
                    // remove last ,
                    this.metricsHeaderStr = this.metricsHeaderStr.substring(0, this.metricsHeaderStr.length() - 1);
                }

            }else{
                this.metricsHeaderStr = reader.readLine();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public MetricsSample readSample() {
        try {

            String metricsStr = null;
            metricsStr = reader.readLine();
            if (metricsStr != null) {
               // return marshaller.unmarshallSample(metricsHeaderStr,metricsStr);
            } else {
                //TODO do something when file is done
            }

        } catch (IOException e2) {
            // sometthing went wrong with filehandling
            e2.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // mmh...
        return null;
    }
}


