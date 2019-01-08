package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by anton on 13.02.17.
 */
public class MultiplexDistributor implements ScriptableDistributor {

    private Collection<Pair<String, Pipeline>> subPipelines = null;

    /**
     * This constructor requires a call to setSubPipelines()
     */
    public MultiplexDistributor() {
    }

    public MultiplexDistributor(List<Pipeline> pipes) {
        setSubPipelineList(pipes);
    }

    @Override
    public String toString() {
        return String.format("Multiplex (%s sub pipelines)", subPipelines.size());
    }

    public void setSubPipelineList(List<Pipeline> pipes) {
        subPipelines = new ArrayList<>(pipes.size());
        int key = 0;
        for (Pipeline pipe : pipes) {
            subPipelines.add(new Pair<>(String.valueOf(key++), pipe));
        }
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> newSubPipelines) throws IOException {
        subPipelines = new ArrayList<>();
        for (Pair<String, PipelineBuilder> p : newSubPipelines) {
            subPipelines.add(new Pair<>(p.getLeft(), p.getRight().build()));
        }
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        return subPipelines;
    }

    @Override
    public Collection<Object> formattedChildren() {
        return ScriptableDistributor.formattedStaticSubPipelines(subPipelines);
    }

}
