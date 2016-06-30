package metrics.algorithms;

import metrics.Header;
import metrics.Sample;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/11/16.
 * <p>
 * Only pass out a single metric identified by column index.
 */
public class MetricFilterAlgorithm extends AbstractAlgorithm {

    private final MetricFilter filter;
    private Header lastHeader = null;
    private Header outputHeader = null;
    private boolean[] includedMetrics = null;

    public interface MetricFilter {
        boolean shouldInclude(String name, int column);

        default void hashParameters(ParameterHash hash) {
            hash.writeClassName(this);
        }
    }

    public MetricFilterAlgorithm(MetricFilter filter) {
        this.filter = filter;
    }

    public MetricFilterAlgorithm(String... excludedCols) {
        this(new MetricNameFilter(excludedCols));
    }

    public static class MetricNameFilter implements MetricFilter {
        private final Set<String> excluded;

        public MetricNameFilter(String... excludedCols) {
            excluded = new HashSet<>(Arrays.asList(excludedCols));
        }

        @Override
        public boolean shouldInclude(String name, int column) {
            return !excluded.contains(name);
        }

        public void hashParameters(ParameterHash hash) {
            MetricFilter.super.hashParameters(hash);
            hash.writeInt(excluded.size());
            for (String str : excluded)
                hash.writeChars(str);
        }
    }

    public static final class MetricNameIncludeFilter extends MetricNameFilter {

        public MetricNameIncludeFilter(String... excludedCols) {
            super(excludedCols);
        }

        @Override
        public boolean shouldInclude(String name, int column) {
            return !super.shouldInclude(name, column);
        }

    }

    @Override
    public String toString() {
        return "metric filter";
    }

    protected Sample executeSample(Sample sample) throws IOException {
        sample.checkConsistency();
        if (sample.headerChanged(lastHeader))
            outputHeader = buildHeader(sample.getHeader());

        double values[] = new double[outputHeader.header.length];
        double inputMetrics[] = sample.getMetrics();
        int j = 0;
        for (int i = 0; i < includedMetrics.length; i++) {
            if (includedMetrics[i])
                values[j++] = inputMetrics[i];
        }
        return new Sample(outputHeader, values, sample);
    }

    private Header buildHeader(Header newHeader) {
        List<String> fields = new ArrayList<>();
        includedMetrics = new boolean[newHeader.header.length];
        for (int i = 0; i < newHeader.header.length; i++) {
            String field = newHeader.header[i];
            boolean shouldInclude = filter.shouldInclude(field, i);
            if (shouldInclude) fields.add(field);
            includedMetrics[i] = shouldInclude;
        }
        return new Header(fields.toArray(new String[fields.size()]));
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        filter.hashParameters(hash);
    }

}
