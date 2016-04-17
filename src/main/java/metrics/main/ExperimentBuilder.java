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
public class ExperimentBuilder extends AbstractExperimentBuilder {

    public ExperimentBuilder(Config config, AbstractExperimentBuilder.Host host, boolean printFiles) throws IOException {
        super(readCsvFiles(config.experimentFolder, host, printFiles));
    }

    public String getName() {
        return "new";
    }

    private static FileMetricReader readCsvFiles(String rootDir, AbstractExperimentBuilder.Host host, boolean printFiles) throws IOException {
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

    private static void addAllHostData(FileMetricReader reader, String rootDir, AbstractExperimentBuilder.Host host) throws IOException {
        String hostName = "\\Q" + host.name + "\\E";
        String scenarios = "(global|" + host.layer + "/" + hostName + ")";
        String experiment = "[^/]*";
        String metrics = "metrics\\." + hostName + "/[^/]*\\.csv$";
        String pattern = ".*/" + scenarios + "/" + experiment + "/" + metrics;
        reader.addFiles(rootDir, Pattern.compile(pattern));
    }

    public static FileMetricReader.NameConverter scenarioName() {
        return file -> {
            Path path = file.toPath();
            int num = path.getNameCount();
            return path.subpath(num - 3, num - 2).toString();
        };
    }

    public static abstract class Factory implements DimensionReductionApp.ExperimentBuilderFactory {

        public List<Host> getAllHosts() {
            return Arrays.asList(
                    new Host("bono.ims", "virtual"),
                    new Host("ellis.ims", "virtual"),
                    new Host("homer.ims", "virtual"),
                    new Host("hs.ims", "virtual"),
                    new Host("ns.ims", "virtual"),
                    new Host("ralf.ims", "virtual"),
                    new Host("sprout.ims", "virtual"),

                    new Host("wally131", "physical"),
                    new Host("wally134", "physical"),
                    new Host("wally135", "physical"),
                    new Host("wally136", "physical"),
                    new Host("wally137", "physical"),
                    new Host("wally139", "physical"),
                    new Host("wally141", "physical"),
                    new Host("wally142", "physical"),
                    new Host("wally145", "physical"),
                    new Host("wally146", "physical"),
                    new Host("wally147", "physical"),
                    new Host("wally148", "physical")
            );
        }

    }


}
