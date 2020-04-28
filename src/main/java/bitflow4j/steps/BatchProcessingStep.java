package bitflow4j.steps;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.Context;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instead of immediately handling every Sample, fill up a list of samples based on a number of criteria (time, tag values, ...).
 * The resulting list is then processed through a number of BatchHandlers, which possibly output a different number of samples in the end.
 * Afterwards, the resulting list of samples is forwarded to the subsequent processing step in the resulting order.
 * <p>
 * The class is marked as final, because the obsolete way to use it was subclassing. Now, one or more BatchHandler instances must be added
 * through the addBatchHandler() method instead.
 * <p>
 * Created by anton on 5/8/16.
 */
public abstract class BatchProcessingStep extends AbstractProcessingStep {

    static final Logger logger = Logger.getLogger(BatchProcessingStep.class.getName());

    final long concurrentCheckTimeout;
    final String batchSeparationTag;
    final long timeoutMs;

    long lastSampleReceived = -1;
    boolean warnedMissingSeparationTag = false;
    String previousSeparationTagValue = null;

    final List<Sample> window = new ArrayList<>();

    public BatchProcessingStep(String batchSeparationTag, long timeoutMs) {
        this.concurrentCheckTimeout = timeoutMs / 2;
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void initialize(Context context) throws IOException {
        // TODO refactor for allowing additional flushing modes:
        // window size, timeout (wall clock, sample timestamps), tag change. Micro batching ("jumping" window) vs moving window.
        initializeConcurrentChecks();

        super.initialize(context);
    }

    private void initializeConcurrentChecks() throws IOException {
        if (concurrentCheckTimeout > 0) {

            // TODO Implement Daemon-thread that triggers concurrent flush similar to the following

//            synchronized (BatchProcessingStep.this) {
//                checkConcurrentFlush();
//            }
//            return pool.sleep(concurrentCheckTimeout);
        }
    }

    public abstract void checkConcurrentFlush();

    public abstract List<Sample> handleBatch(List<Sample> batch);

    protected final synchronized void flushWindow(List<Sample> window) throws IOException {
        if (window.isEmpty())
            return;
        printFlushMessage(window);

        window = handleBatch(window);
        for (Sample sample : window) {
            this.output(sample);
        }
    }

    private void printFlushMessage(List<Sample> window) {
        String info = "";
        if (window.isEmpty()) {
            info = " (no samples)";
        } else {
            int numSamples = window.size();
            int numMetrics = window.get(0).getMetrics().length;
            info += "(" + numSamples + " samples, " + numMetrics + " metrics)";
        }
        logger.info(toString() + " computing results " + info);
    }

    public static abstract class Batching extends BatchProcessingStep {

        public Batching(String batchSeparationTag, long timeoutMs) {
            super(batchSeparationTag, timeoutMs);
        }

        @Override
        public void checkConcurrentFlush() {
            if (lastSampleReceived > 0 && System.currentTimeMillis() - lastSampleReceived > timeoutMs) {
                try {
                    boolean flushed = flush();
                    if (flushed) {
                        logger.log(Level.INFO, String.format("Flushed batch due to timeout (%sms) for step: %s.", timeoutMs, toString()));
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Failed to automatically flush batch", ex);
                }
            }
        }

        @Override
        public void handleSample(Sample sample) throws IOException {
            // TODO is new Date().getTime() better than currentTimeMillis()? Also check other usages of currentTimeMillis().
            lastSampleReceived = System.currentTimeMillis();
            if (shouldFlush(sample)) {
                flush();
            }
            window.add(sample);
        }

        @Override
        public void cleanup() throws IOException {
            flush();
        }

        private boolean flush() throws IOException {
            if (window.isEmpty())
                return false;
            flushWindow(window);
            window.clear();
            return true;
        }

        private boolean shouldFlush(Sample sample) {
            if (batchSeparationTag != null && !batchSeparationTag.isEmpty()) {
                if (!sample.hasTag(batchSeparationTag)) {
                    if (!warnedMissingSeparationTag) {
                        logger.warning("BatchPipelineStep: Dropping samples without '" + batchSeparationTag + "' tag");
                        warnedMissingSeparationTag = true;
                    }
                } else {
                    String tagValue = sample.getTag(batchSeparationTag);
                    boolean tagChanged = previousSeparationTagValue != null && !tagValue.equals(previousSeparationTagValue);
                    previousSeparationTagValue = tagValue;
                    return tagChanged;
                }
            }
            return false;
        }

    }

    public abstract static class Merging extends BatchProcessingStep {

        private final Map<String, Long> timeoutMap = new ConcurrentHashMap<>();
        private final Map<String, List<Sample>> samplesForTag = new ConcurrentHashMap<>();
        private final List<String> tagList = new ArrayList<>();

        public Merging(String batchSeparationTag, long timeoutMs, boolean mergeMode) {
            super(batchSeparationTag, timeoutMs);
        }

        @Override
        public void handleSample(Sample sample) {
            if (timeoutMap.get(sample.getTag(batchSeparationTag)) != null) {
                samplesForTag.get(sample.getTag(batchSeparationTag)).add(sample);
            } else {
                //Put Sample into timeoutMap and create new ArrayList with Sample
                timeoutMap.put(sample.getTag(batchSeparationTag), System.currentTimeMillis() + timeoutMs);
                List<Sample> newList = new ArrayList<>();
                newList.add(sample);
                samplesForTag.put(sample.getTag(batchSeparationTag), newList);
                tagList.add(sample.getTag(batchSeparationTag));
            }
        }

        @Override
        public void checkConcurrentFlush() {
            try {
                flushMapResults(System.currentTimeMillis());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to automatically flush batch", e);
            }
        }

        @Override
        public void cleanup() throws IOException {
            // Flush all remaining samples.
            flushMapResults(-1);
        }

        private void flushMapResults(long currentTime) throws IOException {
            if (tagList.isEmpty())
                return;

            //Check for Lists to be flushed according to timeout
            Iterator<String> tagIter = tagList.iterator();
            for (String tagValue = tagIter.next(); tagIter.hasNext(); tagValue = tagIter.next()) {
                long endTime = timeoutMap.get(tagValue);
                if (endTime < currentTime || currentTime == -1) {
                    Sample exampleSample = samplesForTag.get(tagValue).get(0);
                    logger.log(Level.INFO, String.format("Flushing one result, tag %s=%s, number of tags: %s, sample tags: %s", batchSeparationTag, tagValue, timeoutMap.size(), exampleSample.getTags()));
                    //Flush this window
                    flushWindow(samplesForTag.get(tagValue));
                    timeoutMap.remove(tagValue);
                    samplesForTag.remove(tagValue);
                    tagIter.remove();

                } else {
                    //List 'tagList' keeps sorting of timeouts indirectly, so we can break the for loop after first not-timed-out sample
                    break;
                }
            }
        }
    }

}
