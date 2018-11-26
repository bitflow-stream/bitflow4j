package bitflow4j.steps.normalization;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.steps.batch.TrainableWindow;
import bitflow4j.window.AbstractSampleWindow;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;
import bitflow4j.window.SampleWindow;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractFeatureScaler extends TrainableWindow {

    private static final Logger logger = Logger.getLogger(AbstractFeatureScaler.class.getName());
    private final MultiHeaderWindow<MetricStatisticsWindow> window = new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);
    // TODO have to store data twice for stable headers...
    private final SampleWindow samples = new SampleWindow();
    private Map<String, MetricScaler> scalers = new HashMap<>();

    public AbstractFeatureScaler(int trainingInstances) {
        super(trainingInstances);
    }

    public AbstractFeatureScaler() {
        this(0);
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
    protected void flushResults() throws IOException {
        buildScalers();
        for (Sample sample : samples.samples) {
            outputSingleInstance(sample);
        }
        /*
          BEGIN alt solution without SampleWindow Warning: this works different than the current feature scaler (will add default values
          for missing metrics using the multiheaderwindow
         */
//        int numSamplesInWindow = window.numSamples();
//        int numScalers = scalers.size();
//        Header outHeader = window.getHeader();
//        MetricStatisticsWindow[] metricWindows = new MetricStatisticsWindow[numScalers];
//        window.allMetricWindows().toArray(metricWindows);
//        for (int i = 0; i < numSamplesInWindow; i++) {
//            double[] scaledMetrics = new double[numScalers];
//            for (int k = 0; k < numScalers; k++) {
//                MetricScaler scaler = scalers.get(k);
//                MetricWindow metricWindow = metricWindows[k];
//                scaledMetrics[k] = scaler.scale(metricWindow.getValue(i));
//            }
//            Sample constructedSample = new Sample(outHeader, scaledMetrics, window.makeSample(i));
//            output.writeSample(constructedSample);
//        }
        /*
          end alt solution without SampleWindow
         */
    }

    @Override
    protected void outputSingleInstance(Sample sample) throws IOException {
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
            //TODO: this seems to be a dead end as all metrics pass the MultiHeaderWindow before any call to this method
            logger.warning("Warning: Metrics could not be scaled: " + missingMetrics);
        }
        output.writeSample(new Sample(header, values, sample));
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
            if (stdDev == 0) {
                stdDev = 1;
            }
            stdDeviation = stdDev;
            if (stats.totalMinimum < stats.totalMaximum) {
                min = stats.totalMinimum;
                max = stats.totalMaximum;
            } else {
                min = max = 0;
            }
        }
    }
}
