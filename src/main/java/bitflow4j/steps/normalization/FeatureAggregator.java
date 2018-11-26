package bitflow4j.steps.normalization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 4/21/16.
 */
public class FeatureAggregator extends AbstractPipelineStep {

    public static final Aggregator AVG = MetricStatisticsWindow::average;
    public static final Aggregator SLOPE = MetricStatisticsWindow::slope;
    // TODO allow multiple window sizes
    private final MultiHeaderWindow<MetricStatisticsWindow> window;
    private final List<Aggregator> aggregators = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private Header incomingHeader = null;
    private Header outgoingHeader = null;

    public FeatureAggregator(int windowSize) {
        window = new MultiHeaderWindow<>(windowSize, MetricStatisticsWindow.FACTORY);
    }

    public FeatureAggregator(long windowTimespan) {
        window = new MultiHeaderWindow<>(windowTimespan, MetricStatisticsWindow.FACTORY);
    }

    public FeatureAggregator addAvg() {
        return add("avg", AVG);
    }

    public FeatureAggregator addSlope() {
        return add("slope", SLOPE);
    }

    public FeatureAggregator add(String name, Aggregator agg) {
        if (names.contains(name))
            throw new IllegalArgumentException("Name '" + name + "' already present in this FeatureAggregator");
        aggregators.add(agg);
        names.add(name);
        return this;
    }

    public void writeSample(Sample sample) throws IOException {
        window.add(sample);
        if (sample.headerChanged(incomingHeader))
            outgoingHeader = constructHeader();

        // Call every aggregation for every metric
        double values[] = new double[outgoingHeader.header.length];

        int i = 0;
        int j = 0;
        double incoming[] = sample.getMetrics();
        for (MetricStatisticsWindow stat : window.allMetricWindows()) {
            values[i++] = incoming[j++];
            for (Aggregator agg : aggregators)
                values[i++] = agg.aggregate(stat);
        }
        output.writeSample(new Sample(outgoingHeader, values, sample));
    }

    private Header constructHeader() {
        String headerFields[] = new String[window.numMetrics() * (1 + names.size())];
        int i = 0;
        for (String name : window.allMetricNames()) {
            headerFields[i++] = name;
            for (String aggName : names)
                headerFields[i++] = name + "_" + aggName;
        }
        return new Header(headerFields);
    }

    @Override
    public String toString() {
        String res = "aggregator [";
        boolean added = false;
        for (String name : names) {
            if (added) res += ", ";
            added = true;
            res += name;
        }
        return res + "]";
    }

    public interface Aggregator {
        double aggregate(MetricStatisticsWindow window);
    }

}
