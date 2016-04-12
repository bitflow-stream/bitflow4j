package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by anton on 4/11/16.
 */
public class CorrelationAlgorithm extends PostAnalysisAlgorithm<CorrelationAlgorithm.MetricLog> {

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

    private final Correlation[] correlations;
    private static final String sourceSeparator = " <-> ";

    public CorrelationAlgorithm(Correlation ...correlations) {
        super();
        this.correlations = correlations;
    }

    public String toString() {
        return "correlation algorithm " + Arrays.toString(correlations);
    }

    public CorrelationAlgorithm() {
        this(Pearson, Spearmans, Kendalls);
    }

    private Sample.Header makeHeader() {
        String[] headerFields = new String[correlations.length];
        for (int i = 0; i < correlations.length; i++) {
            headerFields[i] = correlations[i].toString();
        }

        // Include the source field in the header, which will indicate the metric-combination for the correlation(S)
        return new Sample.Header(headerFields, Sample.Header.HEADER_SOURCE_IDX + 1);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Date timestamp = new Date();
        Sample.Header header = makeHeader();

        // Iterate every combination of metrics once
        for (String metric1 : metrics.keySet()) {
            for (String metric2 : metrics.keySet()) {
                if (metric1.compareTo(metric2) < 0) {
                    double vector1[] = getStats(metric1).getVector();
                    double vector2[] = getStats(metric2).getVector();
                    double corr[] = new double[correlations.length];
                    for (int i = 0; i < corr.length; i++) {
                        corr[i] = correlations[i].correlation(vector1, vector2);
                    }
                    Sample sample = new Sample(header, corr, timestamp, metric1 + sourceSeparator + metric2);
                    output.writeSample(sample);
                }
            }
        }
    }

    @Override
    protected MetricLog createMetricStats(String name) {
        return new MetricLog(name);
    }

    // SpearmansCorrelation does not allow NaN, so use 0.0 instead.
    static class MetricLog extends PostAnalysisAlgorithm.MetricLog {

        MetricLog(String name) {
            super(name);
        }

        @Override
        double fillValue() {
            return 0.0;
        }

    }

}
