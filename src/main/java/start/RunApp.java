package start;

import Algorithms.NoopAlgorithm;
import Marshaller.BinaryMarshaller;
import Marshaller.CsvMarshaller;
import Marshaller.Marshaller;
import Marshaller.TextMarshaller;
import MetricIO.FileMetricsReader;
import MetricIO.InputStreamProducer;
import MetricIO.MetricPrinter;
import MetricIO.TcpMetricsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final String CSV_PATH = "/home/anton/software/monitoring-data";
    static final String OUT_PATH = "/home/anton/test.out";

    static final OutputStream output;
    static {
        try {
//        output = System.out;
            output = new FileOutputStream(OUT_PATH, false);
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

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

    private static FileMetricsReader readCsvFiles(String path) throws IOException {
        FileMetricsReader reader = new FileMetricsReader(new CsvMarshaller(), FileMetricsReader.FILE_PATH);
        reader.addFiles(path,
            (p, attr) -> {
                if (attr.isDirectory()) {
                    return !p.getFileName().toString().equals("analysis"); // Skip analysis dirs
                } else if (attr.isRegularFile()) {
                    return p.getFileName().toString().endsWith("csv");
                }
                return false;
            });

        for (File f : reader.files) {
            if (f.getPath().contains("analysis")) {
                throw new IllegalStateException("WRONG: " + f.toString());
            }
        }

        return reader;
    }

    private static InputStreamProducer createTcpStream(int port, Marshaller marshaller) throws IOException {
        return new TcpMetricsListener(port, marshaller);
    }

    public static void main(String[] args){
        AppBuilder builder = new AppBuilder();
        try {
//            builder.addInputProducer(createTcpStream(PORT, getMarshaller(MARSHALLER)));
            builder.addInputProducer(readCsvFiles(CSV_PATH));
            builder.addAlgorithm(new NoopAlgorithm());
            builder.setOutput(new MetricPrinter(output, getMarshaller(OUTPUT_MARSHALLER)));
            builder.runApp();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
