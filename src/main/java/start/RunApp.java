package start;

import Algorithms.NoopAlgorithm;
import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller;
import Marshaller.TextMarshaller;
import MetricIO.InputStreamProducer;
import MetricIO.MetricPrinter;
import MetricIO.TcpMetricsListener;

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

    private static InputStreamProducer createTcpStream(int port, Marshaller marshaller) throws IOException {
        TcpMetricsListener listener = new TcpMetricsListener(port, marshaller);
        System.err.println("Listening on port " + port);
        return listener;
    }

    public static void main(String[] args){
        AppBuilder builder = new AppBuilder();
        try {
            Marshaller marshaller = getMarshaller(MARSHALLER);
            InputStreamProducer producer = createTcpStream(PORT, marshaller);
            builder.addInputProducer(producer);
            builder.addAlgorithm(new NoopAlgorithm());
            builder.setOutput(new MetricPrinter(getMarshaller(OUTPUT_MARSHALLER)));
            builder.runApp();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
