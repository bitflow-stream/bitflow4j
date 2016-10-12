package metrics.main.prototype;

import metrics.algorithms.TimestampSort;
import metrics.main.AlgorithmPipeline;
import metrics.main.Config;
import metrics.main.analysis.OpenStackSampleSplitter;
import metrics.main.analysis.SourceLabellingAlgorithm;
import metrics.main.data.DataSource;
import metrics.main.data.Host;
import metrics.main.data.NewDataSource;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by anton on 6/13/16.
 */
public class PrepareTrainingData {

    static {
        Config.initializeLogger();
    }

    private static final Logger logger = Logger.getLogger(PrepareTrainingData.class.getName());

    private static final DataSource<Host> data = new NewDataSource("experiments-new-2", false);

    private static final String OUTPUT_FILE_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            logger.severe("Need 3 parameters: <hostname> <virtual|physical> <target-file>");
            return;
        }
        String hostname = args[0];
        String layer = args[1];
        String target = args[2];

        Host host = new Host(hostname, layer);
        new AlgorithmPipeline(data, host)
                .fork(new OpenStackSampleSplitter() /*.fillInfo()*/,
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                throw new IllegalStateException("Received non-default fork from OpenStackSampleSplitter: " + name);
                            }
                            p
                                    .step(new SourceLabellingAlgorithm())
                                    .step(new TimestampSort(true))
                                    .fileOutput(target, OUTPUT_FILE_FORMAT);
                        })
                .runAndWait();
    }

}
