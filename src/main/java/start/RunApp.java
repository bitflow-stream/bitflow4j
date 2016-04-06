package start;

import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller;
import MetricIO.InputStreamClosedException;
import MetricIO.MetricInputStream;
import MetricIO.TcpMetricInputStream;
import Metrics.Sample;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final int PORT = 9999;
//    static final String MARSHALLER = "CSV";
    static final String MARSHALLER = "BIN";

    private static Marshaller getMarshaller(String format) {
        switch(format) {
            case "CSV":
                return new CsvMarshaller();
            case "BIN":
                return new BinaryMarshaller();
            default:
                throw new IllegalStateException("Unknown marshaller format: " + format);
        }
    }

    private static MetricInputStream createTcpStream(int port, Marshaller marshaller) throws IOException {
        TcpMetricInputStream mis = new TcpMetricInputStream(port, marshaller);
        System.err.println("Listening on " + port);

        Thread t = new Thread() {
            public void run() {
                try {
                    mis.acceptConnections();
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
            }
        };
        t.setDaemon(true);
        t.start();

        return mis;
    }

    public static void main(String[] args){
        Marshaller marshaller = getMarshaller(MARSHALLER);
        try {
            MetricInputStream input = createTcpStream(PORT, marshaller);
            while (true) {
                try {
                    Sample sample = input.readSample();
                    System.err.println("Received: " + Arrays.toString(sample.getHeader()));
                    System.err.println("Data: " + Arrays.toString(sample.getMetrics()));
                } catch(InputStreamClosedException e) {
                    // ignore
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
