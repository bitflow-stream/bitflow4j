package bitflow4j.steps;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.task.LoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author kevinstyp
 */
public class MergeBatchPipelineStep extends AbstractBatchPipelineStep {
    final String batchSeparationTag;
    final long timeoutMs;

    private Map<String, Long> timeoutMap = new ConcurrentHashMap<>();
    private Map<String, List<Sample>> samplesForTag = new ConcurrentHashMap<>();
    private List<String> tagList = new ArrayList<>();

    @BitflowConstructor
    public MergeBatchPipelineStep(@Optional String batchSeparationTag, @Optional long timeoutMs) {
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    public MergeBatchPipelineStep(String batchSeparationTag, long timeoutMs, BatchHandler... handlers) {
        super(handlers);
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void checkForFlush(long currentTime) throws IOException {
        flushMapResults(currentTime);
    }

    @Override
    public void addSample(Sample sample) {
        addSampleToMaps(sample);
    }

    @Override
    public void threadIteration(TaskPool pool) throws IOException {
        if (timeoutMs > 0) {
            pool.start(new LoopTask() {
                @Override
                public String toString() {
                    return "Auto-Flush Task for " + MergeBatchPipelineStep.this.toString();
                }

                @Override
                protected boolean executeIteration() throws IOException {
                    long currentTime = System.currentTimeMillis();
                    checkForFlush(currentTime);
                    return pool.sleep(timeoutMs / 2);
                }
            });
        }
    }

    @Override
    public void closeCleanup() throws IOException {
        // Flush all remaining samples.
        flushMapResults(-1);
    }

    private synchronized void flushMapResults(long currentTime) throws IOException {
        //Check for Lists to be flushed according to timeout
        List<String> removeTags = new ArrayList<>();
        for (String tagValue : tagList) {
            long endTime = timeoutMap.get(tagValue);
            if (endTime < currentTime || currentTime == -1) {
                logger.log(Level.INFO, String.format("Flushing one result, Map Size: %s", timeoutMap.size()));
                logger.log(Level.INFO, String.format("Flushing one result, Sample-receive: %s", samplesForTag.get(tagValue).get(0).getTag("received")));
                removeTags.add(tagValue);
                //Flush this window
                for (Sample s : samplesForTag.get(tagValue)) {
                    window.add(s);
                }
                flushResults();
            } else {
                //List 'tagList' keeps sorting of timeouts indirectly, so we can break the for loop after first not-timed-out sample
                break;
            }
        }
        for (String tagValue : removeTags) {
            timeoutMap.remove(tagValue);
            samplesForTag.remove(tagValue);
            tagList.remove(tagValue);
        }
    }

    private void addSampleToMaps(Sample sample) {
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
}
