package bitflow4j.steps.fork.distribute;

import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.script.registry.Description;
import bitflow4j.script.registry.Optional;
import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Description("A Fork that forks the samples based on the provided template. Samples with the same template values of the provided" +
        " template name will be passed to the same fork. Use ${tagName} Syntax to evaluate the given tags value.")
public class ForkTemplate extends AbstractTagFork {

    private final String template;
    protected final boolean wildcardMatch, regexMatch;
    private final Map<String, Pattern> patterns = new HashMap<>();

    public ForkTemplate(String template, @Optional(defaultBool = true) boolean wildcard, @Optional boolean regex) {
        super();
        this.wildcardMatch = wildcard;
        this.regexMatch = regex;
        this.template = template;
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        this.subPipelineBuilders = subPipelines;
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
        return String.format("Distribute template '%s' to %s sub pipelines (wildcards: %s, regex: %s)",
                template, super.subPipelineBuilders.size(), wildcardMatch, regexMatch);
    }

    @Override
    protected String getSampleKey(Sample sample) {
        return sample.resolveTagTemplate(template);
    }

    @Override
    protected boolean matches(String key, String tag) {
        if (wildcardMatch || regexMatch) {
            if (patterns.containsKey(key)) {
                return patterns.get(key).matcher(tag).matches();
            } else {
                throw new IllegalStateException(String.format("No pattern compiled for '%s' (trying to match template '%s')", key, tag));
            }
        } else {
            return key.equals(tag);
        }
    }

}
