package MetricIO;

import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller_Interface;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;


/**
 * Created by fschmidt, mwall on 31.03.16.
 *
 * error handling should be done above this class, is connection is lost catch exeception
 * outside and start new TcpMetricInputStream, new header will be set
 *
 * runs in a thread
 *
 */
public class TcpMetricInputStream implements MetricInputStream {

    private ServerSocket tcpSocket = null;
    private Socket connectionSocket = null;
    private DataInputStream dataInputStream = null;

    private String metricsHeaderStr = "";
    private String metricsStr = "";
    private Marshaller_Interface marshaller = null;


    public TcpMetricInputStream(int port) throws IOException {
        this(port,"CSV");
    }

    public TcpMetricInputStream(int port, String format) throws IOException {

            this.tcpSocket = new ServerSocket(port);
            this.connectionSocket = tcpSocket.accept();
            this.dataInputStream = new DataInputStream(
                    this.connectionSocket.getInputStream());


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
            marshaller.unmarshallSampleHeader(dataInputStream);
    }


    public MetricsSample readSample() throws IOException, ParseException {

        return marshaller.unmarshallSampleMetrics(dataInputStream);
    }
}

