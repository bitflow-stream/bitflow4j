package metrics.main;

import metrics.io.file.FileMetricReader;
import metrics.io.MetricInputAggregator;

import java.io.File;
import java.io.IOException;

/**
 * Created by anton on 4/16/16.
 */
public abstract class AbstractExperimentBuilder extends AppBuilder {

    public AbstractExperimentBuilder(MetricInputAggregator aggregator) {
        super(aggregator);
    }

    public AbstractExperimentBuilder(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        super(csvFile, conv);
    }

    public AbstractExperimentBuilder(FileMetricReader fileInput) {
        super(fileInput);
    }

    public AbstractExperimentBuilder(int port, String inputMarshaller) throws IOException {
        super(port, inputMarshaller);
    }

    public static class Host {

        public final String layer;
        public final String name;

        public Host(String name, String layer) {
            this.layer = layer;
            this.name = name;
        }

        public String toString() {
            return layer + " host " + name;
        }

    }

}
