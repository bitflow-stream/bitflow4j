package bitflow4j.steps;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.script.registry.RegisteredParameter;
import bitflow4j.script.registry.RegisteredParameterList;
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

    private final String tag;
    private final long timeoutMs;

    private final Map<String, Long> timeoutMap = new ConcurrentHashMap<>();
    private final Map<String, List<Sample>> samplesForTag = new ConcurrentHashMap<>();
    private final List<String> tagList = new ArrayList<>();

    @BitflowConstructor
    public MergeBatchPipelineStep(String tag, @Optional long timeoutMs) {
        this(tag, timeoutMs, new BatchHandler[0]);
    }

    public MergeBatchPipelineStep(String tag, long timeoutMs, BatchHandler... handlers) {
        super(handlers);
        this.tag = tag;
        this.timeoutMs = timeoutMs;
    }

    public static final RegisteredParameterList PARAMETER_LIST = new RegisteredParameterList(
            new RegisteredParameter[]{
                    new RegisteredParameter("tag", RegisteredParameter.ContainerType.Primitive, String.class),
                    new RegisteredParameter("timeout", RegisteredParameter.ContainerType.Primitive, Long.class, 0L),
                    mergeModeParameter
            });

    public static MergeBatchPipelineStep createFromParameters(Map<String, Object> params) {
        String tag = null;
        if (params.containsKey("tag")) {
            tag = (String) params.get("tag");
        }
        long timeout = 0;
        if (params.containsKey("timeout")) {
            timeout = (Long) params.get("timeout");
        }
        return new MergeBatchPipelineStep(tag, timeout);
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
                flushWindow(samplesForTag.get(tagValue));
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
        if (timeoutMap.get(sample.getTag(tag)) != null) {
            samplesForTag.get(sample.getTag(tag)).add(sample);
        } else {
            //Put Sample into timeoutMap and create new ArrayList with Sample
            timeoutMap.put(sample.getTag(tag), System.currentTimeMillis() + timeoutMs);
            List<Sample> newList = new ArrayList<>();
            newList.add(sample);
            samplesForTag.put(sample.getTag(tag), newList);
            tagList.add(sample.getTag(tag));
        }
    }
}
