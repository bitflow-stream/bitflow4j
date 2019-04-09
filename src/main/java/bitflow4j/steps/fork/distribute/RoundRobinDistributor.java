package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
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
        setSubPipelineList(pipelines);
    }

    public String toString() {
        return String.format("Round Robin (%s cycles)", subPipelines.size());
    }

    public void setSubPipelineList(List<Pipeline> pipelines) {
        subPipelines = pipelines.stream().map(p -> Collections.singletonList(new Pair<>("", p))).collect(Collectors.toList());
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        Set<String> ignoredKeys = new HashSet<>();
        for (Pair<String, PipelineBuilder> pipe : subPipelines) {
            if (!pipe.getLeft().isEmpty()) {
                ignoredKeys.add(pipe.getLeft());
            }
            this.subPipelines.add(Collections.singleton(new Pair<>(pipe.getLeft(), pipe.getRight().build())));
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
        // TODO this formatting is not correct: it lumps together the sub-pipeline collections for every rr-cycle
        Collection<Pair<String, Pipeline>> formatted = new ArrayList<>();
        for (Collection<Pair<String, Pipeline>> c : subPipelines) {
            formatted.addAll(c);
        }
        return ScriptableDistributor.formattedStaticSubPipelines(formatted);
    }
}
