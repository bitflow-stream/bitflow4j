package start;

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

    private static MetricInputStream createTcpStream(int port) throws IOException {
        Marshaller marshaller = new CsvMarshaller();
        TcpMetricInputStream mis = new TcpMetricInputStream(PORT, marshaller);
        System.err.println("Listening on " + PORT);

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
        try {
            MetricInputStream input = createTcpStream(PORT);
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
