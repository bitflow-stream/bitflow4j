package metrics.main;

import metrics.CsvMarshaller;
import metrics.io.FileMetricReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/14/16.
 */
public class ExperimentBuilder extends AppBuilder {

    public static class Host {
        public final String layer;
        public final String name;
        public Host(String name, String layer) {
            this.layer = layer;
            this.name = name;
        }
    }

    public ExperimentBuilder(Config config, Host host, boolean printFiles) throws IOException {
        super(readCsvFiles(config.experimentFolder, host, printFiles));
    }

    private static FileMetricReader readCsvFiles(String rootDir, Host host, boolean printFiles) throws IOException {
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

    private static void addAllHostData(FileMetricReader reader, String rootDir, Host host) throws IOException {
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

}