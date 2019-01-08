package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class TagDistributor implements ScriptableDistributor {

    private static final Logger logger = Logger.getLogger(TagDistributor.class.getName());

    private final boolean wildcardMatch, regexMatch;
    private final String tag;
    private final Map<String, Pipeline> subPipelines = new HashMap<>();
    private final Map<String, Collection<Pair<String, Pipeline>>> tagValueCache = new HashMap<>(); // Additional cache just for reducing object creation
    private final Map<String, Pattern> patterns = new HashMap<>();

    private Collection<Pair<String, PipelineBuilder>> subPipelineBuilders;
    private List<String> availableKeys;

    public TagDistributor(String tag) {
        this(tag, true, false);
    }

    public TagDistributor(String tag, boolean wildcard, boolean regex) {
        this.tag = tag;
        this.wildcardMatch = wildcard;
        this.regexMatch = regex;
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        this.subPipelineBuilders = subPipelines;
        availableKeys = subPipelines.stream().map(Pair::getLeft).sorted().collect(Collectors.toList());
        compilePatterns();
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
        return String.format("Distribute tag '%s' to %s sub pipelines (wildcards: %s, regex: %s)",
                tag, subPipelineBuilders.size(), wildcardMatch, regexMatch);
    }

    @Override
    public Collection<Object> formattedChildren() {
        return ScriptableDistributor.formattedSubPipelines(subPipelineBuilders);
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        String value = sample.getTags().get(tag);
        if (tagValueCache.containsKey(value)) {
            return tagValueCache.get(value);
        } else {
            List<Pair<String, Pipeline>> result = new ArrayList<>();
            for (Pair<String, PipelineBuilder> available : subPipelineBuilders) {
                if (matches(available.getLeft(), value)) {
                    Pipeline pipeline = getPipeline(available.getLeft(), value, available.getRight());
                    if (pipeline != null) {
                        result.add(new Pair<>(available.getLeft(), pipeline));
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
        if (subPipelines.containsKey(key)) {
            return subPipelines.get(key);
        }

        try {
            Pipeline pipe = builder.build();
            subPipelines.put(key, pipe);
            return pipe;
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Failed to build sub-pipeline for tag value '%s' (matching %s)", tagValue, key), e);
            return null;
        }
    }

    private boolean matches(String key, String tag) {
        if (wildcardMatch || regexMatch) {
            if (patterns.containsKey(key)) {
                return patterns.get(key).matcher(tag).matches();
            } else {
                throw new IllegalStateException(String.format("No pattern compiled for '%s' (trying to match tag '%s')", key, tag));
            }
        } else {
            return key.equals(tag);
        }
    }

}
