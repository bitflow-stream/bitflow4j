package metrics.main.data;

import metrics.io.aggregate.InputStreamProducer;
import metrics.io.aggregate.MetricInputAggregator;
import metrics.io.aggregate.SequentialAggregator;
import metrics.main.Config;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by anton on 4/17/16.
 */
public abstract class DataSource<T> {

    public abstract List<T> getAllSources();

    public abstract InputStreamProducer createProducer(T source) throws IOException;

    public abstract String toString();

    public MetricInputAggregator preferredAggregator() {
        return new SequentialAggregator();
    }

    public File makeOutputDir(T source) throws IOException {
        String filename = Config.getInstance().getOutputFolder() + "/" + toString() + "/" + source.toString();
        File result = new File(filename);
        if (result.exists() && !result.isDirectory())
            throw new IOException("Not a directory: " + filename);
        if (!result.exists() && !result.mkdirs())
            throw new IOException("Failed to create output directory " + filename);
        return result;
    }

}
