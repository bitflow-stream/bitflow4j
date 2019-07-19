package bitflow4j.steps.fork.distribute;

import bitflow4j.script.registry.Description;
import bitflow4j.script.registry.Optional;

@Description("A Fork that forks the samples based on the provided tag. Samples with the same tag values of the provided" +
        " tag name will be passed to the same fork.")
public class ForkTag extends ForkTemplate {

    public ForkTag(String tag, @Optional(defaultBool = true) boolean wildcard, @Optional boolean regex) {
        super("${" + tag + "}", wildcard, regex);
    }

}
