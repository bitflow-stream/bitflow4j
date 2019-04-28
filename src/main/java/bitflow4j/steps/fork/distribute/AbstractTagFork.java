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
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Description("A Fork that forks the samples based on the provided template. Samples with the same template values of the provided" +
        " template name will be passed to the same fork. Use ${tagName} Syntax to evaluate the given tags value.")
public abstract class AbstractTagFork implements ScriptableDistributor {

    private static final Logger logger = Logger.getLogger(AbstractTagFork.class.getName());

    private final boolean wildcardMatch, regexMatch;
    //private final String template;
    private final Map<String, Pipeline> subPipelines = new HashMap<>();
    private final Map<String, Collection<Pair<String, Pipeline>>> tagValueCache = new HashMap<>(); // Additional cache just for reducing object creation
    private final Map<String, Pattern> patterns = new HashMap<>();

    private Collection<Pair<String, PipelineBuilder>> subPipelineBuilders;
    private List<String> availableKeys;
    private Collection<Object> formattedSubPipelines;

    /*public AbstractTagFork(String template) {
        this(template, true, false);
    }*/

    public AbstractTagFork(boolean wildcard, boolean regex) {
        //this.template = template;
        this.wildcardMatch = wildcard;
        this.regexMatch = regex;
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        this.subPipelineBuilders = subPipelines;
        System.out.println("trololol");
        logger.warning("There is something wrong");
        availableKeys = subPipelines.stream().map(Pair::getLeft).sorted().collect(Collectors.toList());
        compilePatterns();
        formattedSubPipelines = ScriptableDistributor.formattedSubPipelines(subPipelineBuilders);
    }

    private void compilePatterns() throws IOException {
        if (wildcardMatch || regexMatch) {
            for (Pair<String, PipelineBuilder> available : subPipelineBuilders) {
                String key = available.getLeft();
                try {
                    patterns.put(key, Pattern.compile(toWildcardRegex(key)));
                } catch (PatternSyntaxException e) {
                    throw new IOException(e);
                }

            }
        }
    }

    private String toWildcardRegex(String key) {
        if (wildcardMatch) {
            return key.replaceAll("\\*", ".*");
        }
        return key;
    }

    @Override
    public String toString() {
        return "";
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
            if(subPipelineBuilders != null){
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

    /*private boolean matches(String key, String tag) {
        if (wildcardMatch || regexMatch) {
            if (patterns.containsKey(key)) {
                return patterns.get(key).matcher(tag).matches();
            } else {
                throw new IllegalStateException(String.format("No pattern compiled for '%s' (trying to match template '%s')", key, tag));
            }
        } else {
            return key.equals(tag);
        }
    }*/

    protected String getSampleKey(Sample sample){
        return null;
    }

    protected boolean matches(String key, String tag){
        return false;
    }

}
