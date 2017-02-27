package bitflow4j.algorithms.fork;

import bitflow4j.algorithms.AbstractAlgorithm;
import bitflow4j.sample.Sample;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by anton on 27.02.17.
 */
public abstract class SampleMerger extends AbstractAlgorithm {

    private static final Logger logger = Logger.getLogger(SampleMerger.class.getName());

    private Map<String, Queue<Sample>> inputs = new HashMap<>();
    private final int minInputs;
    private final String keyTag;
    private boolean warnedMissingTag = false;

    public SampleMerger(String keyTag, int minInputs) {
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
        List<Sample> samples = new ArrayList<>();
        for (Queue<Sample> input : inputs.values()) {
            samples.add(input.poll());
        }
        Sample merged = mergeSamples(samples);
        super.writeSample(merged);
    }

}
