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
