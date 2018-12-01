package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by anton on 13.02.17.
 */
public class MultiplexDistributor extends ScriptableDistributor.Default {

    /**
     * This constructor requires a call to setSubPipelines()
     */
    public MultiplexDistributor() {
    }

    public MultiplexDistributor(List<Pipeline> pipes) {
        subPipelines = new ArrayList<>(pipes.size());
        int key = 0;
        for (Pipeline pipe : pipes) {
            subPipelines.add(new Pair<>(String.valueOf(key++), pipe));
        }
    }

    @Override
    public String toString() {
        return String.format("Multiplex (%s sub pipelines)", subPipelines.size());
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        return subPipelines;
    }

}
