package metrics.main;

import metrics.main.analysis.OpenStackSampleSplitter;
import metrics.main.data.DataSource;
import metrics.main.data.Host;
import metrics.main.data.NewDataSource;

import java.io.IOException;

/**
 * Created by anton on 6/13/16.
 */
public class PreparePrototypeData {

    private static final DataSource<Host> data = new NewDataSource("experiments-new-2", false);

    private static final String OUTPUT_FILE_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Need 3 parameters: <hostname> <virtual|physical> <target-file>");
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
                            p.fileOutput(target, OUTPUT_FILE_FORMAT);
                        })
                .runAndWait();
    }

}
