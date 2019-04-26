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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//TODO: Change
@Description("A Fork that forks the samples based on the provided tag-name. Samples with the tag will be forked to the 'present' fork." +
        "Samples without the tag will be forked to the 'missing' fork.")
public class ForkTagByPresence implements ScriptableDistributor {

    private static final Logger logger = Logger.getLogger(ForkTagByPresence.class.getName());

    private final String tagName;
    private final Map<String, Pipeline> subPipelines = new HashMap<>();
    private final Map<String, Collection<Pair<String, Pipeline>>> tagValueCache = new HashMap<>(); // Additional cache just for reducing object creation

    private Collection<Pair<String, PipelineBuilder>> subPipelineBuilders;
    private List<String> availableKeys;
    private Collection<Object> formattedSubPipelines;

    public ForkTagByPresence(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        this.subPipelineBuilders = subPipelines;
        availableKeys = subPipelines.stream().map(Pair::getLeft).sorted().collect(Collectors.toList());
        formattedSubPipelines = ScriptableDistributor.formattedSubPipelines(subPipelineBuilders);
    }

    @Override
    public String toString() {
        return String.format("Distribute samples by the presence of the given tag to forks 'missing' and 'present'.");
    }

    @Override
    public Collection<Object> formattedChildren() {
        return formattedSubPipelines;
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        final boolean present = sample.hasTag(tagName);
        String value = present ? "present" : "missing";

        if (tagValueCache.containsKey(value)) {
            return tagValueCache.get(value);
        } else {
            List<Pair<String, Pipeline>> result = new ArrayList<>();
            if(subPipelineBuilders != null){
                for (Pair<String, PipelineBuilder> available : subPipelineBuilders) {
                    if (matches(available.getLeft(), present)) {
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

    private boolean matches(String key, boolean present) {
        if(present) return key.equals("present");
        else return key.equals("missing");
    }

}
