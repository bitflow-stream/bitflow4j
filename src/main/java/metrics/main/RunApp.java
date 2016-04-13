package metrics.main;

import metrics.CsvMarshaller;
import metrics.algorithms.PCAAlgorithm;
import metrics.io.FileMetricReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author fschmidt
 */
public class RunApp {

    private static final String resource_bundle_name = "config";
    private static ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle(resource_bundle_name);
        } catch (MissingResourceException exc) {
            System.err.println("ResourceBundle '" + resource_bundle_name + "' was not found," +
                    "make sure config.properties exists inside src/main/resources");
            System.exit(1);
        }
    }

    private static final String EXPERIMENT_FOLDER = resources.getString("experiment_dir");
    private static final String OUTPUT_FOLDER = resources.getString("output_dir");
    private static final String OUTPUT_FILE = OUTPUT_FOLDER + "/output.csv";

    private static void addAllHostDataCode(FileMetricReader reader, String results, String host) throws IOException {
        reader.addFiles(EXPERIMENT_FOLDER,
                (p, attr) -> {
                    String name = p.getFileName().toString();
                    if (attr.isDirectory()) {
                        if (name.startsWith("metrics."))
                            return name.equals("metrics." + host);
                        if (name.contains("results"))
                            return name.equals(results);
                        if (name.equals("analysis"))
                            return false;
                        return true;
                    } else if (attr.isRegularFile()) {
                        return name.endsWith("csv");
                    }
                    return false;
                });
    }

    private static void addAllHostData(FileMetricReader reader, String host, boolean latest, boolean allExperiments) throws IOException {
        String experiment = allExperiments ? "[^/]*" : "(global-[^/]*|local-[^/]*-" + host + ")";
        String results = latest ? "latest-results" : "results-[^/]*";
        host = "\\Q" + host + "\\E";
        reader.addFiles(EXPERIMENT_FOLDER, Pattern.compile(
                ".*/" + experiment + "/" + results + "/metrics\\." + host + "/[^/]*\\.csv$"
        ));
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
            if (scenario.startsWith("local-")) {
                scenario = scenario.substring(0, scenario.lastIndexOf("-"));
            }
            scenario = scenario.substring(scenario.indexOf("-") + 1);
            return scenario;
        };
    }

    private static FileMetricReader readCsvFiles(String host, boolean printFiles) throws IOException {
        FileMetricReader.NameConverter conv = scenarioName();
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        addAllHostData(reader, host, true, false);
        System.err.println("Reading " + reader.size() + " files");
        if (printFiles)
            for (File f : reader.getFiles()) {
                System.err.println(f.toString());
            }
        return reader;
    }

    public static void main(String[] args) throws IOException {
        //    String host = "wally131";
        String host = "bono.ims";

//        AppBuilder builder = new AppBuilder(9999, "BIN");
        AppBuilder builder = new AppBuilder(readCsvFiles(host, false));
//        builder.setUnifiedSource(HOST);

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
//        builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
        builder.addAlgorithm(new PCAAlgorithm(-1));

        builder.setConsoleOutput("CSV");
//        builder.setFileOutput(OUTPUT_FILE, "CSV");

        builder.runApp();
    }

}
