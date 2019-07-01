package bitflow4j.steps.fork.distribute;

import bitflow4j.script.registry.Description;

@Description("A Fork that forks the samples based on the provided tag. Samples with the same tag values of the provided" +
        " tag name will be passed to the same fork.")
public class ForkTag extends ForkTemplate {

    public ForkTag(String tag) {
        this(tag, true, false);
    }

    public ForkTag(String tag, boolean wildcard, boolean regex) {
        super("${" + tag + "}", wildcard, regex);
    }

}
