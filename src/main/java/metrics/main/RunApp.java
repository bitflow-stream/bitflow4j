package metrics.main;

import metrics.algorithms.NoopAlgorithm;
import metrics.BinaryMarshaller;
import metrics.CsvMarshaller;
import metrics.Marshaller;
import metrics.TextMarshaller;
import metrics.io.FileMetricReader;
import metrics.io.MetricPrinter;
import metrics.io.TcpMetricsListener;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final String CSV_PATH = "/home/anton/software/monitoring-data/experiments/global-overload/latest-results";
    static final String OUT_PATH = "/home/anton/test.out";

    static final int PORT = 9999;
//    static final String INPUT_MARSHALLER = "CSV";
    static final String INPUT_MARSHALLER = "BIN";

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

    private static FileMetricReader readCsvFiles(String path) throws IOException {
        FileMetricReader.NameConverter conv = file -> {
            Path path1 = file.toPath();
            int num = path1.getNameCount();
            String host = path1.subpath(num - 2, num - 1).toString();
            String scenario = path1.subpath(num - 4, num - 3).toString();
            return scenario + "/" + host;
        };
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        reader.addFiles(path,
            (p, attr) -> {
                if (attr.isDirectory()) {
                    return !p.getFileName().toString().equals("analysis"); // Skip analysis dirs
                } else if (attr.isRegularFile()) {
                    return p.getFileName().toString().endsWith("csv");
                }
                return false;
            });
        System.err.println("Reading " + reader.size() + " files");
        return reader;
    }

    private static AppBuilder filesApp() throws IOException {
        AppBuilder builder = new AppBuilder(false);
        builder.addInputProducer(readCsvFiles(CSV_PATH));
        return builder;
    }

    private static AppBuilder tcpApp() throws IOException {
        AppBuilder builder = new AppBuilder(false);
        builder.addInputProducer(new TcpMetricsListener(PORT, getMarshaller(INPUT_MARSHALLER)));
        return builder;
    }

    public static void main(String[] args){
        final boolean FILES = true;
//        final boolean FILES = false;

        final boolean TCP = !FILES;
        final boolean CONSOLE = !FILES;

        AppBuilder builder;
        try {
            if (TCP) {
                builder = tcpApp();
            } else {
                builder = filesApp();
            }

            builder.addAlgorithm(new NoopAlgorithm());

            if (CONSOLE) {
                builder.setOutput(new MetricPrinter(getMarshaller(OUTPUT_MARSHALLER)));
            } else {
                builder.setOutput(new MetricPrinter(OUT_PATH, getMarshaller(OUTPUT_MARSHALLER)));
            }
            builder.runApp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
