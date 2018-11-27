package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/11/16.
 * <p>
 * Only pass out a single metric identified by column index.
 */
public class MetricFilter extends AbstractPipelineStep {

    private final MetricSelection filter;
    private Header lastHeader = null;
    private Header outputHeader = null;
    private boolean[] includedMetrics = null;

    public MetricFilter(MetricSelection filter) {
        this.filter = filter;
    }

    public MetricFilter(String... excludedCols) {
        this(new MetricNameSelection(excludedCols));
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (sample.headerChanged(lastHeader)) {
            outputHeader = buildHeader(sample.getHeader());
        }

        double values[] = new double[outputHeader.header.length];
        double inputMetrics[] = sample.getMetrics();
        int j = 0;
        for (int i = 0; i < includedMetrics.length; i++) {
            if (includedMetrics[i]) {
                values[j++] = inputMetrics[i];
            }
        }
        output.writeSample(new Sample(outputHeader, values, sample));
    }

    private Header buildHeader(Header newHeader) {
        List<String> fields = new ArrayList<>();
        includedMetrics = new boolean[newHeader.header.length];

        for (int i = 0; i < newHeader.header.length; i++) {
            String field = newHeader.header[i];
            boolean shouldInclude = filter.shouldInclude(field);
            if (shouldInclude) {
                fields.add(field);
            }
            includedMetrics[i] = shouldInclude;
        }
        return new Header(fields.toArray(new String[fields.size()]));
    }

    public interface MetricSelection {

        boolean shouldInclude(String name);
    }

    public static class MetricNameSelection implements MetricSelection {

        private final Set<String> excluded;

        public MetricNameSelection(String... excludedCols) {
            excluded = new HashSet<>(Arrays.asList(excludedCols));
        }

        @Override
        public boolean shouldInclude(String name) {
            return !excluded.contains(name);
        }

    }

    public static class MetricRegexSelection implements MetricSelection {

        private final List<Pattern> included = new ArrayList<>();

        public MetricRegexSelection(String... includeRegexes) {
            for (String regexString : includeRegexes) {
                included.add(Pattern.compile(regexString));
            }
        }

        @Override
        public boolean shouldInclude(String name) {
            for (Pattern regex : included) {
                if (regex.matcher(name).matches()) {
                    return true;
                }
            }
            return false;
        }

    }

    public static class MetricRegexExcludeSelection implements MetricSelection {

        private final List<Pattern> excluded = new ArrayList<>();

        public MetricRegexExcludeSelection(String... excludeRegexes) {
            for (String regexString : excludeRegexes) {
                excluded.add(Pattern.compile(regexString));
            }
        }

        @Override
        public boolean shouldInclude(String name) {
            for (Pattern regex : excluded) {
                if (regex.matcher(name).matches()) {
                    return false;
                }
            }
            return true;
        }

    }

    public static final class MetricNameIncludeSelection extends MetricNameSelection {

        public MetricNameIncludeSelection(String... includedCols) {
            super(includedCols);
        }

        @Override
        public boolean shouldInclude(String name) {
            return !super.shouldInclude(name);
        }

    }

    public static class AnyMetricSelection implements MetricSelection {

        @Override
        public boolean shouldInclude(String name) {
            return true;
        }

    }

    @Override
    protected void doClose() throws IOException {
        super.doClose();
    }
}
