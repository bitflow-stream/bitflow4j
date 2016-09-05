package metrics.main.data;

import metrics.CsvMarshaller;
import metrics.io.aggregate.InputStreamProducer;
import metrics.io.file.FileMetricReader;
import metrics.main.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/14/16.
 * <p>
 * This represents the experiment folder structure of the first obsolete of experiment results (experiments-old/)
 */
public class OldDataSource extends DataSource<Host> {

    private final boolean printFiles;
    private final boolean latestResults;
    private final boolean allExperiments;
    private final String experimentSubdir;

    public OldDataSource(String experimentSubdir, boolean latestResults, boolean allExperiments, boolean printFiles) {
        this.printFiles = printFiles;
        this.allExperiments = allExperiments;
        this.latestResults = latestResults;
        this.experimentSubdir = experimentSubdir;
    }

    @Override
    public InputStreamProducer createProducer(Host host) throws IOException {
        return readCsvFiles(experimentSubdir, host.name, latestResults, allExperiments, printFiles);
    }

    public String toString() {
        return experimentSubdir;
    }

    private static FileMetricReader readCsvFiles(String experimentSubdir, String host,
                                                 boolean latestResults, boolean allExperiments, boolean printFiles) throws IOException {
        FileMetricReader.NameConverter conv = scenarioName();
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        addAllHostData(reader, Config.getInstance().getExperimentSubfolder(experimentSubdir),
                host, latestResults, allExperiments);
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
    public List<Host> getAllSources() {
        return Arrays.asList(
                new Host("bono.ims", "virtual"),
                new Host("ellis.ims", "virtual"),
                new Host("homer.ims", "virtual"),
                new Host("hs.ims", "virtual"),
                new Host("ns.ims", "virtual"),
                new Host("ralf.ims", "virtual"),
                new Host("sprout.ims", "virtual"),

                new Host("wally131", "physical"),
                new Host("wally132", "physical"),
                new Host("wally133", "physical"),
                new Host("wally134", "physical"),
                new Host("wally135", "physical"),
                new Host("wally136", "physical"),
                new Host("wally137", "physical"),
                new Host("wally138", "physical"),
                new Host("wally139", "physical"),
                new Host("wally140", "physical"),
                new Host("wally141", "physical"),
                new Host("wally142", "physical"),
                new Host("wally143", "physical"),
                new Host("wally144", "physical"),
                new Host("wally145", "physical"),
                new Host("wally146", "physical"),
                new Host("wally147", "physical"),
                new Host("wally148", "physical"),
                new Host("wally149", "physical"),
                new Host("wally150", "physical")
        );

    }

}
