package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.script.registry.Description;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Description("A Fork that forks the samples based on the provided template. Samples with the same template values of the provided" +
        " template name will be passed to the same fork. Use ${tagName} Syntax to evaluate the given tags value.")
public abstract class AbstractTagFork implements ScriptableDistributor {

    private static final Logger logger = Logger.getLogger(AbstractTagFork.class.getName());

    private final Map<String, Pipeline> subPipelines = new HashMap<>();
    private final Map<String, Collection<Pair<String, Pipeline>>> tagValueCache = new HashMap<>(); // Additional cache just for reducing object creation

    protected Collection<Pair<String, PipelineBuilder>> subPipelineBuilders;
    protected List<String> availableKeys;
    protected Collection<Object> formattedSubPipelines;

    public AbstractTagFork() {}

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        this.subPipelineBuilders = subPipelines;
        availableKeys = subPipelines.stream().map(Pair::getLeft).sorted().collect(Collectors.toList());
        formattedSubPipelines = ScriptableDistributor.formattedSubPipelines(subPipelineBuilders);
    }

    @Override
    public Collection<Object> formattedChildren() {
        return formattedSubPipelines;
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        String value = getSampleKey(sample);
        if (value == null) {
            value = "";
            //TODO How to handle samples without the tag?
        }
        if (tagValueCache.containsKey(value)) {
            return tagValueCache.get(value);
        } else {
            List<Pair<String, Pipeline>> result = new ArrayList<>();
            if (subPipelineBuilders != null) {
                for (Pair<String, PipelineBuilder> available : subPipelineBuilders) {
                    if (matches(available.getLeft(), value)) {
                        Pipeline pipeline = getPipeline(available.getLeft(), value, available.getRight());
                        if (pipeline != null) {
                            result.add(new Pair<>(value, pipeline));
                        }
                    }
                }
            }
            if (result.isEmpty()) {
                logger.warning(String.format("No sub-pipeline for value %s (available keys: %s)", value, availableKeys));
            }
            tagValueCache.put(value, result);
            return result;
        }
    }

    private Pipeline getPipeline(String key, String tagValue, PipelineBuilder builder) {
        if (subPipelines.containsKey(tagValue)) {
            return subPipelines.get(tagValue);
        }

        try {
            Pipeline pipe = builder.build();
            subPipelines.put(tagValue, pipe);
            return pipe;
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Failed to build sub-pipeline for template value '%s' (matching %s)", tagValue, key), e);
            return null;
        }
    }

    protected abstract String getSampleKey(Sample sample);

    protected abstract boolean matches(String key, String tag);

}
