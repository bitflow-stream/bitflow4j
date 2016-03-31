package MetricIO;

import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller_Interface;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


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

            // thread waits for header input from the outside
            new Thread() {
                public void run() {
                    try {
                        if (format == "BIN") {
                            while (true) {
                                BufferedReader inFromClient = null;
                                inFromClient = new BufferedReader(
                                        new InputStreamReader(connectionSocket.getInputStream()));

                                String tmpHeader = "";
                                while (!(tmpHeader = inFromClient.readLine()).isEmpty()) {
                                    metricsHeaderStr += tmpHeader + ",";
                                    // remove last ,
                                    metricsHeaderStr = metricsHeaderStr.substring(0, metricsHeaderStr.length() - 1);
                                }
                            }
                        } else {
                            Socket connectionSocket = tcpSocket.accept();
                            BufferedReader inFromClient = new BufferedReader(
                                    new InputStreamReader(connectionSocket.getInputStream()));
                            metricsHeaderStr = inFromClient.readLine();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
    }


    public MetricsSample readSample() {


        new Thread() {
            public void run() {
                try {
                    // TODO need to be tested , need to read one line at this point
                    metricsStr = dataInputStream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return marshaller.marshallSample(this.metricsHeaderStr,metricsStr);
    }
}

