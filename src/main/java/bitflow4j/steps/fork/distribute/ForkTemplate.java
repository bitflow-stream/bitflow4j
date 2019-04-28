package bitflow4j.steps.fork.distribute;

import bitflow4j.Sample;
import bitflow4j.script.registry.Description;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Description("A Fork that forks the samples based on the provided template. Samples with the same template values of the provided" +
        " template name will be passed to the same fork. Use ${tagName} Syntax to evaluate the given tags value.")
public class ForkTemplate extends AbstractTagFork {

    private final String template;
    private final Map<String, Pattern> patterns = new HashMap<>();

    public ForkTemplate(String template) {
        this(template, true, false);
    }

    public ForkTemplate(String template, boolean wildcard, boolean regex) {
        super(wildcard, regex);
        this.template = template;
    }

    @Override
    public String toString() {
        return String.format("Distribute template '%s' to %s sub pipelines (wildcards: %s, regex: %s)",
                template, super.subPipelineBuilders.size(), super.wildcardMatch, super.regexMatch);
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
