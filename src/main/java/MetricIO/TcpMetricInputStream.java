package MetricIO;

import Marshaller.Marshaller;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


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

    private Marshaller marshaller = null;
    private String[] header;

    public TcpMetricInputStream(int port, Marshaller marshaller) throws IOException {

        this.tcpSocket = new ServerSocket(port);

        while(true) {
            this.connectionSocket = tcpSocket.accept();

            if (this.connectionSocket.isConnected()) {
                System.out.println("connection accepted.");
                break;
            }
        }

        this.marshaller = marshaller;
        this.dataInputStream = new DataInputStream(this.connectionSocket.getInputStream());

        header = marshaller.unmarshallHeader(dataInputStream);

    }

    public MetricsSample readSample() throws IOException {

        MetricsSample sample =  marshaller.unmarshallSample(dataInputStream, this.header);
        return sample;
    }
}

