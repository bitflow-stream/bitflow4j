package start;

import Algorithms.NoopAlgorithm;
import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller;
import Marshaller.TextMarshaller;
import MetricIO.MetricInputStream;
import MetricIO.MetricPrinter;
import MetricIO.TcpMetricInputStream;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final int PORT = 9999;
//    static final String MARSHALLER = "CSV";
    static final String MARSHALLER = "BIN";

    static final String OUTPUT_MARSHALLER = "CSV";
//    static final String OUTPUT_MARSHALLER = "TXT";

    private static Marshaller getMarshaller(String format) {
        switch(format) {
            case "CSV":
                return new CsvMarshaller();
            case "BIN":
                return new BinaryMarshaller();
            case "TXT":
                return new TextMarshaller();
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
        AppBuilder builder = new AppBuilder();
        try {
            Marshaller marshaller = getMarshaller(MARSHALLER);
            MetricInputStream input = createTcpStream(PORT, marshaller);
            builder.addInput(input);
            builder.addAlgorithm(new NoopAlgorithm());
            builder.setOutput(new MetricPrinter(getMarshaller(OUTPUT_MARSHALLER)));
            builder.runApp();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
