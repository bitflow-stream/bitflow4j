package metrics.main;

import metrics.CsvMarshaller;
import metrics.io.file.FileMetricReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/14/16.
 * <p>
 * This represents the experiment folder structure of the first batch of experiment results (experiments-old/)
 */
public class OldExperimentData extends ExperimentData {

    private final Config config;
    private final boolean printFiles;
    private final boolean latestResults;
    private final boolean allExperiments;

    public OldExperimentData(Config config, boolean latestResults, boolean allExperiments, boolean printFiles) {
        this.config = config;
        this.printFiles = printFiles;
        this.allExperiments = allExperiments;
        this.latestResults = latestResults;
    }

    @Override
    public AppBuilder makeBuilder(Host host) throws IOException {
        return new AppBuilder(readCsvFiles(config, host.name, latestResults, allExperiments, printFiles));
    }

    public String toString() {
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

    @Override
    public List<ExperimentData.Host> getAllHosts() {
        return Arrays.asList(
                new ExperimentData.Host("bono.ims", "virtual"),
                new ExperimentData.Host("ellis.ims", "virtual"),
                new ExperimentData.Host("homer.ims", "virtual"),
                new ExperimentData.Host("hs.ims", "virtual"),
                new ExperimentData.Host("ns.ims", "virtual"),
                new ExperimentData.Host("ralf.ims", "virtual"),
                new ExperimentData.Host("sprout.ims", "virtual"),

                new ExperimentData.Host("wally131", "physical"),
                new ExperimentData.Host("wally132", "physical"),
                new ExperimentData.Host("wally133", "physical"),
                new ExperimentData.Host("wally134", "physical"),
                new ExperimentData.Host("wally135", "physical"),
                new ExperimentData.Host("wally136", "physical"),
                new ExperimentData.Host("wally137", "physical"),
                new ExperimentData.Host("wally138", "physical"),
                new ExperimentData.Host("wally139", "physical"),
                new ExperimentData.Host("wally140", "physical"),
                new ExperimentData.Host("wally141", "physical"),
                new ExperimentData.Host("wally142", "physical"),
                new ExperimentData.Host("wally143", "physical"),
                new ExperimentData.Host("wally144", "physical"),
                new ExperimentData.Host("wally145", "physical"),
                new ExperimentData.Host("wally146", "physical"),
                new ExperimentData.Host("wally147", "physical"),
                new ExperimentData.Host("wally148", "physical"),
                new ExperimentData.Host("wally149", "physical"),
                new ExperimentData.Host("wally150", "physical")
        );

    }

}
