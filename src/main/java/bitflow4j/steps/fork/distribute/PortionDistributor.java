package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 13.02.17.
 */
public class PortionDistributor implements ScriptableDistributor {

    public static final String MAIN_KEY = "primary";
    public static final String SECONDARY_KEY = "secondary";

    private List<Pair<String, Pipeline>> primary = new ArrayList<>();
    private List<Pair<String, Pipeline>> secondary = new ArrayList<>();
    private final float redirectedPortion;
    private final Random rnd = new Random();

    public PortionDistributor(float redirectedPortion) {
        if (redirectedPortion < 0 || redirectedPortion > 1) {
            throw new IllegalArgumentException("redirectedPortion must be in 0..1: " + redirectedPortion);
        }
        this.redirectedPortion = redirectedPortion;
    }

    public PortionDistributor(float redirectedPortion, Pipeline primary, Pipeline secondary) {
        this(redirectedPortion);
        addPrimary(primary);
        addSecondary(secondary);
    }

    public void addPrimary(Pipeline pipeline) {
        this.primary.add(new Pair<>(MAIN_KEY, pipeline));

    }

    public void addSecondary(Pipeline pipeline) {
        this.secondary.add(new Pair<>(SECONDARY_KEY, pipeline));
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        Set<String> unsupportedKeys = new HashSet<>();
        for (Pair<String, PipelineBuilder> pipe : subPipelines) {
            if (pipe.getLeft().equals(MAIN_KEY)) {
                addPrimary(pipe.getRight().build());
            } else if (pipe.getLeft().equals(SECONDARY_KEY)) {
                addSecondary(pipe.getRight().build());
            } else {
                unsupportedKeys.add(pipe.getLeft());
            }
        }
        if (!unsupportedKeys.isEmpty()) {
            logger.warning(String.format("%s only supports sub-pipelines keys '%s' and '%s', the following keys will be ignored: %s",
                    this, MAIN_KEY, SECONDARY_KEY, unsupportedKeys));
        }
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        return rnd.nextFloat() < redirectedPortion ? primary : secondary;
    }

    @Override
    public Collection<Object> formattedChildren() {
        Collection<Pair<String, Pipeline>> subPipelines = new ArrayList<>();
        subPipelines.addAll(primary);
        subPipelines.addAll(secondary);
        return ScriptableDistributor.formattedStaticSubPipelines(subPipelines);
    }
}
