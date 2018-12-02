package bitflow4j.steps.fork.distribute;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TagDistributor extends ScriptableDistributor.Default {

    private final boolean wildcardMatch, regexMatch;
    private final String tag;
    private final Map<String, Collection<Pair<String, Pipeline>>> subPipelineCache = new HashMap<>();
    private final Map<String, Pattern> patterns = new HashMap<>();

    public TagDistributor(String tag) {
        this(tag, true, false);
    }

    public TagDistributor(String tag, boolean wildcard, boolean regex) {
        this.tag = tag;
        this.wildcardMatch = wildcard;
        this.regexMatch = regex;
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) throws IOException {
        super.setSubPipelines(subPipelines);
        compilePatterns();
    }

    private void compilePatterns() throws IOException {
        if (wildcardMatch || regexMatch) {
            for (Pair<String, Pipeline> available : subPipelines) {
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
                tag, subPipelines.size(), wildcardMatch, regexMatch);
    }

    @Override
    public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
        String value = sample.getTags().get(tag);
        if (subPipelineCache.containsKey(value)) {
            return subPipelineCache.get(value);
        } else {
            List<Pair<String, Pipeline>> result = new ArrayList<>();
            for (Pair<String, Pipeline> available : subPipelines) {
                if (matches(available.getLeft(), value))
                    result.add(available);
            }
            if (result.isEmpty()) {
                logger.warning(String.format("No sub-pipeline for value %s (available keys: %s)", value, availableKeys));
            }
            subPipelineCache.put(value, result);
            return result;
        }
    }

    private boolean matches(String key, String tag) {
//        key = toWildcardRegex(key);
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
