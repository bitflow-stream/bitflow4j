package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by anton on 13.02.17.
 */
public class RoundRobinDistributor implements ScriptableDistributor {

    private int counter = 0;
    private List<Collection<Pair<String, Pipeline>>> subPipelines = new ArrayList<>();

    public RoundRobinDistributor() {
    }

    public RoundRobinDistributor(List<Pipeline> pipelines) {
        setSubPipelines(pipelines.stream().map(p -> new Pair<>("", p)).collect(Collectors.toList()));
    }

    public String toString() {
        return String.format("Round Robin (%s subPipelines)", subPipelines.size());
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) {
        Set<String> ignoredKeys = new HashSet<>();
        for (Pair<String, Pipeline> pipe : subPipelines) {
            if (!pipe.getLeft().isEmpty()) {
                ignoredKeys.add(pipe.getLeft());
            }
            this.subPipelines.add(Collections.singleton(pipe));
        }
        if (!ignoredKeys.isEmpty()) {
            logger.warning(String.format("%s: pipeline keys are ignored: %s", this, ignoredKeys));
        }
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        if (subPipelines.size() == 0)
            return null;
        int index = counter++ % subPipelines.size();
        return subPipelines.get(index);
    }

    @Override
    public Collection<Object> formattedChildren() {
        return null;
    }
}
