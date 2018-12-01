package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.util.*;

public class TagDistributor extends ScriptableDistributor.Default {

    private String tag;
    private Map<String, Collection<Pair<String, Pipeline>>> subPipelineCache = new HashMap<>();

    public TagDistributor(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return String.format("Distribute tag '%s' to %s sub pipelines", tag, subPipelines.size());
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        String value = sample.getTags().get(tag);
        if (subPipelineCache.containsKey(value)) {
            return subPipelineCache.get(value);
        } else {
            List<Pair<String, Pipeline>> result = new ArrayList<>();
            for (Pair<String, Pipeline> available : subPipelines) {

                // TODO implement wildcard and regex matching

                if (available.getLeft().equals(value)) {
                    result.add(available);
                }
            }
            if (result.isEmpty()) {
                logger.warning(String.format("No sub-pipeline for value %s (available keys: %s)", value, availableKeys));
            }
            subPipelineCache.put(value, result);
            return result;
        }
    }

}
