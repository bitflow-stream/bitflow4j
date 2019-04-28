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

@Description("A Fork that forks the samples based on the provided tag-name. Samples with the tag will be forked to the 'present' fork." +
        "Samples without the tag will be forked to the 'absent' fork.")
public class ForkTagByPresence extends AbstractTagFork {

    private static final Logger logger = Logger.getLogger(ForkTagByPresence.class.getName());

    private final String tagName;
    private final static String TAGPRESENT = "present";
    private final static String TAGABSENT = "absent";


    public ForkTagByPresence(String tagName) {
        super(true, false);
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return String.format("Distribute samples by the presence of the given tag to forks '%s' and '%s'.", TAGPRESENT, TAGABSENT);
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        System.out.println("subPipelines-length:" + subPipelines.size());
        for(Pair pair : subPipelines){
            if(!pair.getLeft().equals(TAGPRESENT) && !pair.getLeft().equals(TAGABSENT)){
                System.out.println("A different identifer was found.");
                logger.warning("A different identifer was found.:");
                throw new IOException(String.format("A different identifer than '%s' and '%s' was found: '%s'",
                        TAGPRESENT, TAGABSENT, pair.getLeft()));
            }
        }
        super.setSubPipelines(subPipelines);
    }

    @Override
    protected String getSampleKey(Sample sample) {
        return sample.hasTag(tagName) ? TAGPRESENT : TAGABSENT;
    }

    @Override
    protected boolean matches(String key, String present) {
        return key.equals(present);
    }

}
