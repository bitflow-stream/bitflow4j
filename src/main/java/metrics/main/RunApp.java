package metrics.main;

import metrics.CsvMarshaller;
import metrics.algorithms.CorrelationAlgorithm;
import metrics.algorithms.StdDeviationFilterAlgorithm;
import metrics.io.FileMetricReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    private static final String CSV_FOLDER = "/home/anton/software/monitoring-data/experiments/";

    private static final String RESULTS = "latest-results";
    private static final String HOST = "bono.ims";

    private static final String OUT_PATH = "/home/anton/test.out";

    private static final int PORT = 9999;
//    static final String INPUT_MARSHALLER = "CSV";
    private static final String INPUT_MARSHALLER = "BIN";

    private static final String OUTPUT_MARSHALLER = "CSV";
    //    static final String OUTPUT_MARSHALLER = "TXT";

    private static final boolean PRINT_FILES = false;

    private static void addAllHostData(FileMetricReader reader, String results, String host) throws IOException {
        reader.addFiles(CSV_FOLDER,
            (p, attr) -> {
                String name = p.getFileName().toString();
                if (attr.isDirectory()) {
                    if (name.startsWith("metrics."))
                        return name.equals("metrics." + host);
                    if (name.contains("results"))
                        return name.equals(results);
                    return !name.equals("analysis");
                } else if (attr.isRegularFile()) {
                    return name.endsWith("csv");
                }
                return false;
            });
    }

    private static FileMetricReader.NameConverter scenarioAndHostName() {
        return file -> {
            Path path = file.toPath();
            int num = path.getNameCount();
            String host = path.subpath(num - 2, num - 1).toString();
            String scenario = path.subpath(num - 4, num - 3).toString();
            return scenario + "/" + host;
        };
    }

    private static FileMetricReader.NameConverter scenarioName() {
        return file -> {
            Path path = file.toPath();
            int num = path.getNameCount();
            String scenario = path.subpath(num - 4, num - 3).toString();
            return scenario;
        };
    }

    private static FileMetricReader readCsvFiles() throws IOException {
        FileMetricReader.NameConverter conv = scenarioName();
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        addAllHostData(reader, RESULTS, HOST);
        System.err.println("Reading " + reader.size() + " files");
        if (PRINT_FILES)
            for (File f : reader.getFiles()) {
                System.err.println(f.toString());
            }
        return reader;
    }

    public static void main(String[] args) throws IOException {
        final boolean TCP = false;
        final boolean CONSOLE = true;

        AppBuilder builder = TCP
                ? new AppBuilder(PORT, INPUT_MARSHALLER)
                : new AppBuilder(readCsvFiles());

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
//        builder.addAlgorithm(new NoopAlgorithm());
        builder.addAlgorithm(new StdDeviationFilterAlgorithm(0.02));
        builder.addAlgorithm(new CorrelationAlgorithm());
//        builder.addAlgorithm(new MetricCounter());

        if (CONSOLE) {
            builder.setConsoleOutput(OUTPUT_MARSHALLER);
        } else {
            builder.setFileOutput(OUT_PATH, OUTPUT_MARSHALLER);
        }
        builder.runApp();
    }

}
