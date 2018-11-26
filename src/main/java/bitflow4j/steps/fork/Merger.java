package bitflow4j.steps.fork;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by anton on 27.02.17.
 */
public abstract class Merger extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(Merger.class.getName());

    private final Map<String, Queue<Sample>> inputs = new HashMap<>();
    private final int minInputs;
    private final String keyTag;
    private boolean warnedMissingTag = false;

    public Merger(String keyTag, int minInputs) {
        this.keyTag = keyTag;
        this.minInputs = minInputs;
    }

    public abstract Sample mergeSamples(List<Sample> samples) throws IOException;

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        if (!sample.hasTag(keyTag)) {
            if (!warnedMissingTag) {
                warnedMissingTag = true;
                logger.warning(this + ": Dropping samples without key-tag '" + keyTag + "'");
            }
            return;
        }

        // Add new sample to the according input queue
        String key = sample.getTag(keyTag);
        Queue<Sample> queue = inputs.get(key);
        if (queue == null) {
            queue = new LinkedList<>();
            inputs.put(key, queue);
        }
        queue.offer(sample);

        // Check if enough input queues have delivered a sample
        if (inputs.size() < minInputs)
            return;
        for (Queue<Sample> input : inputs.values()) {
            if (input.isEmpty())
                return;
        }

        // Merge samples and forward the result
        List<Sample> samples = inputs.values().stream().map(Queue::poll).collect(Collectors.toList());
        Sample merged = mergeSamples(samples);
        super.writeSample(merged);
    }

}
