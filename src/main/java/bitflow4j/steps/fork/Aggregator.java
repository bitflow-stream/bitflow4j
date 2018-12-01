package bitflow4j.steps.fork;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * This pipeline step can receive samples from multiple inputs and aggregate those samples to
 * one big output sample. The output sample is generated and forwarded after every input stream
 * delivers a fresh sample. To avoid frequent header changes in the beginning, a minimum number
 * of input streams is expected to deliver a sample before the first output sample is generated.
 * The samples must contain a configurable "input tag" that is used as a key to group the incoming
 * samples into input streams.
 * <p>
 * Multiple threads and PipelineSteps can push samples into this pipeline step through writeSample().
 * <p>
 * Created by anton on 02.01.17.
 */
public class Aggregator extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(Aggregator.class.getName());

    private final String inputTag;
    private final int minInputs;

    private final TreeMap<String, LinkedList<Sample>> windows = new TreeMap<>();
    private final Map<String, Header> inputHeaders = new HashMap<>();
    private Header outHeader = null;

    public Aggregator(String inputTag, int minInputs) {
        this.inputTag = inputTag;
        this.minInputs = minInputs;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (!sample.hasTag(inputTag)) {
            logger.warning("Dropping sample without input tag '" + inputTag + "'");
            return;
        }
        String stream = sample.getTag(inputTag);
        synchronized (this) {
            LinkedList<Sample> window = getWindow(stream);
            window.add(sample);
            sampleReceived();
        }
    }

    private void sampleReceived() throws IOException {
        if (windows.size() < minInputs) {
            // Still waiting for the minimum number of inputs to send samples.
            logger.fine(this + " waiting for inputs, received " + minInputs + " out of " + windows.size());
            return;
        }
        for (Map.Entry<String, LinkedList<Sample>> window : windows.entrySet()) {
            if (window.getValue().isEmpty()) {
                // Still waiting for this input to deliver a sample.
                logger.fine(this + " waiting for " + window.getKey() + " to deliver a sample...");
                return;
            }
        }
        boolean updateHeader = inputHeaders.size() != windows.size();
        List<Sample> samples = new ArrayList<>(windows.size());
        for (Map.Entry<String, LinkedList<Sample>> window : windows.entrySet()) {
            Sample sample = window.getValue().pop();
            samples.add(sample);
            if (!updateHeader) {
                if (!inputHeaders.get(window.getKey()).equals(sample.getHeader()))
                    updateHeader = true;
            }
        }
        if (updateHeader) {
            outHeader = constructHeader(samples);
        }
        Sample sample = constructSample(samples, outHeader);
        output.writeSample(sample);
    }

    private Header constructHeader(List<Sample> samples) {
        inputHeaders.clear();
        int numFields = samples.stream().map((s) -> s.getHeader().header.length).reduce(0, Integer::sum);

        String[] fields = new String[numFields];
        int i = 0;
        for (Sample sample : samples) {
            String input = sample.getTag(inputTag);
            Header header = sample.getHeader();
            inputHeaders.put(input, header);
            for (String field : header.header) {
                fields[i++] = input + "/" + field;
            }
        }
        logger.info(this + " produces a sample with " + fields.length + " metrics");
        return new Header(fields);
    }

    private Sample constructSample(List<Sample> samples, Header header) {
        double[] values = new double[header.header.length];
        Map<String, String> tags = new HashMap<>();
        Date timestamp = null;
        int i = 0;
        for (Sample sample : samples) {
            for (double value : sample.getMetrics()) {
                values[i++] = value;
            }
            if (timestamp == null || sample.getTimestamp().before(timestamp)) {
                timestamp = sample.getTimestamp();
            }
            String input = sample.getTag(inputTag);
            for (Map.Entry<String, String> tag : sample.getTags().entrySet()) {
                if (!tag.getKey().equals(inputTag)) {
                    tags.put(input + "/" + tag.getKey(), tag.getValue());
                }
            }
        }
        return new Sample(header, values, timestamp, tags);
    }

    private LinkedList<Sample> getWindow(String input) {
        LinkedList<Sample> window;
        if (windows.containsKey(input)) {
            window = windows.get(input);
        } else {
            window = new LinkedList<>();
            windows.put(input, window);
        }
        return window;
    }

    public String toString() {
        return "Aggregator (" + windows.size() + "/" + minInputs + " inputs)";
    }

}
