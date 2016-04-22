package metrics.algorithms;

import metrics.Sample;
import metrics.algorithms.logback.MetricLog;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/12/16.
 * <p>
 * This analyses the output of CorrelationAlgorithm and produces a summary.
 */
public class CorrelationSignificanceAlgorithm extends PostAnalysisAlgorithm<MetricLog> {

    private final double minCorrelationSignificance;
    private final Map<String, Results> results = new HashMap<>();
    private final Set<String> allAlgos = new HashSet<>();

    public CorrelationSignificanceAlgorithm(double minCorrelationSignificance) {
        super(true);
        this.minCorrelationSignificance = minCorrelationSignificance;
    }

    private Results getResults(String name) {
        Results res = results.get(name);
        if (res == null) {
            res = new Results(name);
            results.put(name, res);
        }
        return res;
    }

    @Override
    public void registerMetricData(Sample sample) throws IOException {
        registerSample(sample);
        String label = sample.getLabel();
        String source = sample.getSource();
        Results res = getResults(label);
        String header[] = sample.getHeader().header;
        double values[] = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            String algo = header[i];
            double val = values[i];
            if (val >= minCorrelationSignificance) {
                res.incrementValid(algo, source);
            }
            allAlgos.add(algo);
        }
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        String[] headerFields = allAlgos.toArray(new String[0]);
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.HEADER_LABEL_IDX + 1);

        List<Results> sortedResults = new ArrayList<>(results.values());
        Collections.sort(sortedResults);
        Date timestamp = new Date();
        for (Results res : sortedResults) {
            double values[] = new double[headerFields.length];
            for (int i = 0; i < values.length; i++) {
                Set<String> sources = res.validSources.get(headerFields[i]);
                int num = sources == null ? 0 : sources.size();
                values[i] = (double) num;
            }
            Sample sample = new Sample(header, values, timestamp, null, res.metrics);
            output.writeSample(sample);
        }
    }

    @Override
    public String toString() {
        return "correlation post-analysis [" + minCorrelationSignificance + "]";
    }

    @Override
    protected MetricLog createMetricStats(String name) {
        return new MetricLog(name);
    }

    private static class Results implements Comparable<Results> {

        final String metrics;
        final Map<String, Set<String>> validSources = new HashMap<>();

        private Results(String metrics) {
            this.metrics = metrics;
        }

        void incrementValid(String algo, String source) {
            Set<String> sources = validSources.get(algo);
            if (sources == null) {
                sources = new HashSet<>();
                validSources.put(algo, sources);
            }
            sources.add(source);
        }

        public int compareTo(Results o) {
            // This comparison is the wrong way around to enforce descending order in Collections.sort()
            return Integer.compare(o.size(), size());
        }

        public int size() {
            int size = 0;
            for (Set<String> set : validSources.values()) {
                size += set.size();
            }
            return size;
        }

    }

}
