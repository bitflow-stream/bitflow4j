package bitflow4j.steps.collect;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;

import java.io.IOException;
import java.util.*;

@Description("Collects samples by applying separate timeouts to each new seen specified tag value." +
        "Very useful for collecting samples after a multiplex fork step.")
public class SampleCollector extends AbstractPipelineStep {
    private Map<String, Long> timeoutMap;
    private final int timeoutMs;
    private final String tag;
    private Map<String, List<Sample>> samplesForTag;

    public SampleCollector(String tag, int timeoutMs) {
        this.tag = tag;
        this.timeoutMs = timeoutMs;
        timeoutMap = new HashMap<>();
        samplesForTag = new HashMap<>();
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        //Already saw sample with given tag
        if (timeoutMap.get(sample.getTag(tag)) != null) {
            samplesForTag.get(sample.getTag(tag)).add(sample);
        } else {
            //Put Sample into timeoutMap and create new ArrayList with Sample
            timeoutMap.put(sample.getTag(tag), System.currentTimeMillis() + timeoutMs);
            ArrayList<Sample> newList = new ArrayList<>();
            newList.add(sample);
            samplesForTag.put(sample.getTag(tag), newList);
        }

        //Check for Lists to be flushed according to timeout
        long currentTime = System.currentTimeMillis();
        ArrayList<String> removeTags = new ArrayList<>();
        for (String tagValue : timeoutMap.keySet()) {
            long endTime = timeoutMap.get(tagValue);
            if (endTime < currentTime) {
                removeTags.add(tagValue);
                //Flush this window
                for (Sample s : samplesForTag.get(tagValue)) {
                    output.writeSample(s);
                }
            }
        }
        for (String tagValue : removeTags) {
            timeoutMap.remove(tagValue);
            samplesForTag.remove(tagValue);
        }

    }

}