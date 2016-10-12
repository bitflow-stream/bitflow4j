package metrics.main.prototype;

import metrics.io.file.FileMetricReader;
import metrics.main.Config;
import metrics.main.data.DataSource;
import metrics.main.data.Host;
import metrics.main.data.NewDataSource;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * Created by anton on 6/13/16.
 */
public class ListFiles {

    static {
        Config.initializeLogger();
    }

    private static final Logger logger = getLogger(ListFiles.class.getName());

    private static final DataSource<Host> data = new NewDataSource("experiments-new-2", false);

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            logger.severe("Need 3 parameters: <hostname> <virtual|physical> <separator>");
            return;
        }
        String hostname = args[0];
        String layer = args[1];
        String separator = args[2];

        Host host = new Host(hostname, layer);
        FileMetricReader reader = (FileMetricReader) data.createProducer(host);
        boolean started = false;
        for (File file : reader.getFiles()) {
            // Print to STDOUT instead of logger to enable parsing the output
            if (started)
                System.out.print(separator);
            System.out.print(file.getAbsolutePath());
            started = true;
        }
    }

}
