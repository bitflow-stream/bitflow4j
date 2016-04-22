package metrics.algorithms.logback;

import metrics.Sample;
import metrics.algorithms.GenericAlgorithm;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/21/16.
 * <p>
 * This algorithm keeps a log of all previously received samples. The logs can be flushed periodically if necessary.
 * {@link #executeStep(MetricInputStream, MetricOutputStream)} or {@link #executeSample(Sample)} can be overridden.
 * When overriding {@link #executeStep(MetricInputStream, MetricOutputStream)}, {@link #registerSample(Sample)} must
 * be called.
 */
public abstract class LogbackAlgorithm<M extends MetricLog> extends GenericAlgorithm {

    public final LinkedList<SampleMetadata> samples = new LinkedList<>();
    public final SortedMap<String, M> metrics = new TreeMap<>();

    protected abstract M createMetricStats(String name);

    protected final void execute(MetricInputStream input, MetricOutputStream output) throws IOException {
        super.execute(input, output);
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        Sample sample = input.readSample();
        registerMetricData(sample);
        Sample outputSample = executeSample(sample);
        registerMetricData(sample);
        output.writeSample(outputSample);
    }

    /**
     * Must be called from executeSample(). When overriding this, also call registerSample().
     */
    public void registerMetricData(Sample sample) throws IOException {
        String[] header = sample.getHeader().header;
        Set<String> unhandledStats = new HashSet<>(metrics.keySet());
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            MetricLog metric = getStats(header[i]);
            metric.add(values[i]);
            unhandledStats.remove(metric.name); // Might not be contained
        }
        registerSample(sample);
        for (String unhandledHeader : unhandledStats) {
            metrics.get(unhandledHeader).fill(1);
        }
    }

    /**
     * This should be called from registerMetricData(), in case it is overridden.
     * Add one sample to the queue of samples.
     */
    public void registerSample(Sample sample) {
        samples.offer(new SampleMetadata(sample.getTimestamp(), sample.getSource(), sample.getLabel()));
    }

    /**
     * Drop the given number of samples. The oldest samples will be dropped first.
     */
    public void flushSamples(int numSamples) {
        for (M metric : metrics.values())
            metric.flushSamples(numSamples);
        for (int i = 0; i < numSamples && !samples.isEmpty(); i++)
            samples.remove();
    }

    /**
     * Drop all samples older than numSeconds.
     */
    public void flushOldSamples(long numSeconds) {
        Date now = new Date();
        Date flushTime = new Date(now.getTime() - (numSeconds * 1000));
        int numSamples = 0;
        while (!samples.isEmpty()) {
            SampleMetadata sample = samples.peek();
            // Also flush bogus "future" timestamps to avoid hanging on them.
            if (sample.timestamp.before(flushTime) || sample.timestamp.after(now)) {
                numSamples++;
                samples.remove();
            } else {
                break;
            }
        }
        for (M metric : metrics.values())
            metric.flushSamples(numSamples);
    }

    /**
     * Count how many samples came in in the last numSeconds seconds.
     */
    public int countLatestSamples(long numSeconds) {
        Date now = new Date();
        Date flushTime = new Date(now.getTime() - (numSeconds * 1000));
        int size = samples.size();
        int numSamples = 0;
        for (; numSamples < size; numSamples++) {
            Date timestamp = samples.get(size - 1 - numSamples).timestamp;
            if (timestamp.before(flushTime))
                break;
        }
        return numSamples;
    }

    /**
     * Reconstruct the feature-vector of one sample based on its index.
     */
    public double[] getSampleValues(int sampleNr) {
        double row[] = new double[metrics.size()];
        int metricNr = 0;
        for (MetricLog metricLog : metrics.values()) {
            row[metricNr] = metricLog.getValue(sampleNr);
            metricNr++;
        }
        return row;
    }

    /**
     * Return the MetricLog object for the given metric name (feature).
     */
    public M getStats(String name) {
        M result;
        if ((result = metrics.get(name)) == null) {
            result = createMetricStats(name);
            result.fill(samples.size()); // Fill up missed values
            metrics.put(name, result);
        }
        return result;
    }

    protected Sample.Header constructHeader(int specialFields) {
        String headerFields[] = new String[metrics.size()];
        int i = 0;
        for (M stat : metrics.values()) {
            headerFields[i++] = stat.name;
        }
        return new Sample.Header(headerFields, specialFields);
    }

}
