package metrics.algorithms.analysis;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.MetricStatisticsWindow;
import metrics.io.window.MultiHeaderWindow;
import metrics.main.misc.ParameterHash;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by anton on 4/11/16.
 */
public class CorrelationAlgorithm extends WindowBatchAlgorithm {

    public interface Correlation {
        double correlation(double[] x, double y[]);

        String toString();
    }

    public static final Correlation Pearson = new Correlation() {
        public double correlation(double[] x, double[] y) {
            return new PearsonsCorrelation().correlation(x, y);
        }

        public String toString() {
            return "pearson";
        }
    };
    public static final Correlation Spearmans = new Correlation() {
        public double correlation(double[] x, double[] y) {
            return new SpearmansCorrelation().correlation(x, y);
        }

        public String toString() {
            return "spearmans";
        }
    };
    public static final Correlation Kendalls = new Correlation() {
        public double correlation(double[] x, double[] y) {
            return new KendallsCorrelation().correlation(x, y);
        }

        public String toString() {
            return "kendalls";
        }
    };

    private final MultiHeaderWindow<MetricStatisticsWindow> window =
            new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);

    private final Correlation[] correlations;
    private static final String sourceSeparator = " <-> ";

    public CorrelationAlgorithm(Correlation... correlations) {
        this.correlations = correlations;
    }

    public CorrelationAlgorithm() {
        this(Pearson, Spearmans, Kendalls);
    }

    public String toString() {
        return "correlation algorithm " + Arrays.toString(correlations);
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        for (Correlation corr : correlations)
            hash.writeChars(corr.toString());
    }

    private Header makeHeader() {
        String[] headerFields = new String[correlations.length];
        for (int i = 0; i < correlations.length; i++) {
            headerFields[i] = correlations[i].toString();
        }

        // Include the source field in the header, which will indicate the metric-combination for the correlation(S)
        return new Header(headerFields);
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        Date timestamp = new Date();
        Header header = makeHeader();

        // Iterate every combination of metrics once
        for (String metric1 : window.allMetricNames()) {
            for (String metric2 : window.allMetricNames()) {
                if (metric1.compareTo(metric2) < 0) {
                    double vector1[] = window.getWindow(metric1).getVector();
                    double vector2[] = window.getWindow(metric2).getVector();
                    double corr[] = new double[correlations.length];
                    for (int i = 0; i < corr.length; i++) {
                        double val = correlations[i].correlation(vector1, vector2);
                        if (Double.isNaN(val)) val = 0;
                        corr[i] = val;
                    }
                    Sample sample = new Sample(header, corr, timestamp);
                    sample.setLabel(metric1 + sourceSeparator + metric2);
                    output.writeSample(sample);
                }
            }
        }
    }

}
