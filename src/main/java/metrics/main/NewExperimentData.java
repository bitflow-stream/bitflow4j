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
 */
public class NewExperimentData extends ExperimentData {

    private final Config config;
    private final boolean printFiles;

    public NewExperimentData(Config config, boolean printFiles) {
        this.config = config;
        this.printFiles = printFiles;
    }

    @Override
    public AppBuilder makeBuilder(Host host) throws IOException {
        return new AppBuilder(readCsvFiles(config.experimentFolder, host, printFiles));
    }

    @Override
    public String toString() {
        return "new";
    }

    private static FileMetricReader readCsvFiles(String rootDir, ExperimentData.Host host, boolean printFiles) throws IOException {
        FileMetricReader.NameConverter conv = scenarioName();
        FileMetricReader reader = new FileMetricReader(new CsvMarshaller(), conv);
        addAllHostData(reader, rootDir, host);
        System.err.println("Reading " + reader.size() + " files");
        if (printFiles)
            for (File f : reader.getFiles()) {
                System.err.println(f.toString());
            }
        return reader;
    }

    private static void addAllHostData(FileMetricReader reader, String rootDir, ExperimentData.Host host) throws IOException {
        String hostName = "\\Q" + host.name + "\\E";
        String scenarios = "(global|" + host.layer + "/" + hostName + ")";
        String experiment = "[^/]*";
        String metrics = "metrics\\." + hostName + "/[^/]*\\.csv$";
        String pattern = ".*/" + scenarios + "/" + experiment + "/" + metrics;
        reader.addFiles(rootDir, Pattern.compile(pattern));
    }

    private static FileMetricReader.NameConverter scenarioName() {
        return file -> {
            Path path = file.toPath();
            int num = path.getNameCount();
            return path.subpath(num - 3, num - 2).toString();
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
                new ExperimentData.Host("wally134", "physical"),
                new ExperimentData.Host("wally135", "physical"),
                new ExperimentData.Host("wally136", "physical"),
                new ExperimentData.Host("wally137", "physical"),
                new ExperimentData.Host("wally139", "physical"),
                new ExperimentData.Host("wally141", "physical"),
                new ExperimentData.Host("wally142", "physical"),
                new ExperimentData.Host("wally145", "physical"),
                new ExperimentData.Host("wally146", "physical"),
                new ExperimentData.Host("wally147", "physical"),
                new ExperimentData.Host("wally148", "physical")
        );
    }

}
