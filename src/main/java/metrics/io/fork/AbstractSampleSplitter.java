package metrics.io.fork;

import metrics.Header;
import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 4/28/16.
 * <p>
 * Every incoming Sample is horizontally split into multiple Samples, which are distributed into multiple
 * MetricOutputStreams. Metrics that are not assigned to any OutputStream are dropped.
 */
public abstract class AbstractSampleSplitter extends AbstractFork<String> {

    private static final Logger logger = Logger.getLogger(AbstractSampleSplitter.class.getName());

    private final Map<String, Header> outHeaders = new HashMap<>();
    private final Map<String, List<Integer>> outMetrics = new HashMap<>();
    private Header inputHeader = null;

    public AbstractSampleSplitter(OutputStreamFactory<String> outputFactory) {
        super(outputFactory);
    }

    public AbstractSampleSplitter() {
        super();
    }

    protected abstract void fillHeaders(Header inputHeader,
                                        Map<String, Header> headers, Map<String, List<Integer>> metrics);

    public void writeSample(Sample inputSample) throws IOException {
        if (inputSample.headerChanged(inputHeader)) {
            inputHeader = inputSample.getHeader();
            outHeaders.clear();
            outMetrics.clear();
            fillHeaders(inputHeader, outHeaders, outMetrics);
        }
        double inputValues[] = inputSample.getMetrics();
        for (Map.Entry<String, Header> outHeader : outHeaders.entrySet()) {
            String outputName = outHeader.getKey();
            List<Integer> metricIndices = outMetrics.get(outputName);
            double values[] = new double[metricIndices.size()];
            for (int i = 0; i < metricIndices.size(); i++) {
                Integer metricIndex = metricIndices.get(i);
                values[i] = inputValues[metricIndex];
            }

            Sample sample = new Sample(outHeader.getValue(), values, inputSample);
            MetricOutputStream output;
            try {
                output = getOutputStream(outputName);
            } catch (IOException exc) {
                logger.severe("Failed to create output stream for fork '" + outputName + "': " + exc.getMessage());
                exc.printStackTrace();
                outHeaders.remove(outputName);
                outMetrics.remove(outputName);
                continue;
            }
            output.writeSample(sample);
        }
    }

}
