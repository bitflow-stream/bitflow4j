package bitflow4j.steps.batch;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.io.marshall.InputStreamClosedException;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MultiSampleBatchStep extends AbstractPipelineStep {

    protected abstract void flushBatch(List<Sample> window) throws IOException;

    private final LinkedList<Sample> batch = new LinkedList<>();
    private final String batchSeparationTag;
    private String previousTagValue;
    private boolean warnedSamplesWithoutTag = false;
    private Long timeoutMs = null;
    private long startTime;
    private Thread timeoutThread;
    private boolean interruptThread;

    public MultiSampleBatchStep(String batchSeparationTag, long timeoutMs) {
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
        this.startTime = new Date().getTime();
        interruptThread = false;
        Runnable timeoutRunnable = () -> {
            while (!interruptThread) {
                long currentTime = new Date().getTime();
                if (currentTime - startTime > MultiSampleBatchStep.this.timeoutMs) {
                    try {
                        MultiSampleBatchStep.this.doFlushBatch();
                    }catch (IOException ex) {
                        Logger.getLogger(MultiSampleBatchStep.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    Thread.sleep(MultiSampleBatchStep.this.timeoutMs / 2l);
                }catch (InterruptedException ex) {
                    Logger.getLogger(MultiSampleBatchStep.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timeoutThread = new Thread(timeoutRunnable);
        timeoutThread.start();
    }

    public MultiSampleBatchStep(String batchSeparationTag) {
        this.batchSeparationTag = batchSeparationTag;
    }

    private synchronized void doFlushBatch() throws IOException {
        if (batch.isEmpty()) {
            return;
        }

        flushBatch(batch);
        batch.clear();
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        try {
            sample.checkConsistency();
            String tag = sample.getTag(batchSeparationTag);
            if (tag == null) {
                if (!warnedSamplesWithoutTag) {
                    logger.warning("MultiSampleBatchStep: Dropping samples without '" + batchSeparationTag + "' tag");
                }
                warnedSamplesWithoutTag = true;
                return;
            }
            if (previousTagValue != null && !tag.equals(previousTagValue)) {
                doFlushBatch();
            }
            batch.add(sample);
            startTime = new Date().getTime();
            previousTagValue = tag;
        } catch (InputStreamClosedException closedExc) {
            doFlushBatch();
            throw closedExc;
        }
    }

    @Override
    protected synchronized void doClose() throws IOException {
        doFlushBatch();
        interruptThread = true;
        super.doClose();
    }

}
