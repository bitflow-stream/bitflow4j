package metrics.algorithms.normalization;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.TrainableWindowAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.MetricStatisticsWindow;
import metrics.io.window.MultiHeaderWindow;
import metrics.io.window.SampleWindow;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractFeatureScaler extends TrainableWindowAlgorithm {

    private static final Logger logger = Logger.getLogger(AbstractFeatureScaler.class.getName());

    private Map<String, MetricScaler> scalers = new HashMap<>();

    private final MultiHeaderWindow<MetricStatisticsWindow> window =
            new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);

    // TODO have to store data twice for stable headers...
    private final SampleWindow samples = new SampleWindow();

    public AbstractFeatureScaler(int trainingInstances) {
        super(trainingInstances);
    }

    public AbstractFeatureScaler() {
        this(0);
    }

    public interface MetricScaler {
        double scale(double val);
    }

    public static abstract class AbstractMetricScaler implements MetricScaler {
        public final double stdDeviation;
        public final double average;
        public final double min;
        public final double max;

        public AbstractMetricScaler(MetricStatisticsWindow stats) {
            average = stats.average();
            double stdDev = stats.stdDeviation();
            if (stdDev == 0) stdDev = 1;
            stdDeviation = stdDev;
            if (stats.totalMinimum < stats.totalMaximum) {
                min = stats.totalMinimum;
                max = stats.totalMaximum;
            } else {
                min = max = 0;
            }
        }
    }

    protected abstract MetricScaler createScaler(MetricStatisticsWindow stats);

    public Collection<MetricStatisticsWindow> getAllMetrics() {
        return window.allMetricWindows();
    }

    public Header getHeader() {
        return window.getHeader();
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    public Map<String, MetricScaler> getScalers() {
        return scalers;
    }

    private void buildScalers() {
        Collection<MetricStatisticsWindow> stats = window.allMetricWindows();
        scalers.clear();
        for (MetricStatisticsWindow stat : stats) {
            scalers.put(stat.name, createScaler(stat));
        }
    }

    @Override
    protected void addSample(Sample sample) {
        super.addSample(sample);
        samples.add(sample);
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        buildScalers();
        for (Sample sample : samples.samples) {
            outputSingleInstance(sample, output);
        }
    }

    @Override
    protected void outputSingleInstance(Sample sample, MetricOutputStream output) throws IOException {
        Header header = sample.getHeader();
        double incoming[] = sample.getMetrics();
        double values[] = new double[header.header.length];
        int i = 0;
        Set<String> missingMetrics = new HashSet<>();
        for (String field : header.header) {
            MetricScaler scaler = scalers.get(field);
            if (scaler == null) {
                missingMetrics.add(field);
                values[i] = incoming[i];
            } else {
                values[i] = scaler.scale(incoming[i]);
            }
            i++;
        }
        if (!missingMetrics.isEmpty()) {
            logger.warning("Warning: Metrics could not be scaled: " + missingMetrics);
        }
        output.writeSample(new Sample(header, values, sample));
    }

}
