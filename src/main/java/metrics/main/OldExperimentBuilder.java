package metrics.main;

import metrics.CsvMarshaller;
import metrics.io.FileMetricReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/14/16.
 * <p>
 * This builder represents the experiment folder structure of the first batch of experiment results (experiments-old/)
 */
public class OldExperimentBuilder extends AppBuilder {

    public OldExperimentBuilder(Config config, String host,
                                boolean latestResults, boolean allExperiments, boolean printFiles) throws IOException {
        super(readCsvFiles(config, host, latestResults, allExperiments, printFiles));
    }

    public String getName() {
        return "old";
    }

    private static FileMetricReader readCsvFiles(Config config, String host,
                                                 boolean latestResults, boolean allExperiments, boolean printFiles) throws IOException {
        FileMetricReader.NameConverter conv = scenarioName();
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        addAllHostData(reader, config.oldExperimentFolder, host, latestResults, allExperiments);
        System.err.println("Reading " + reader.size() + " files");
        if (printFiles)
            for (File f : reader.getFiles()) {
                System.err.println(f.toString());
            }
        return reader;
    }

    private static void addAllHostData(FileMetricReader reader,
                                       String rootDir, String host, boolean latest, boolean allExperiments) throws IOException {
        String experiment = allExperiments ? "[^/]*" : "(global-[^/]*|local-[^/]*-" + host + ")";
        String results = latest ? "latest-results" : "results-[^/]*";
        host = "\\Q" + host + "\\E";
        reader.addFiles(rootDir, Pattern.compile(
                ".*/" + experiment + "/" + results + "/metrics\\." + host + "/[^/]*\\.csv$"
        ));
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

}
