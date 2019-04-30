package bitflow4j.steps.fork.distribute;

import bitflow4j.Sample;
import bitflow4j.misc.Pair;
import bitflow4j.script.registry.Description;

import java.io.IOException;
import java.util.Collection;

@Description("A Fork that forks the samples based on the provided tag-name. Samples with the tag will be forked to the 'present' fork." +
        "Samples without the tag will be forked to the 'absent' fork.")
public class ForkTagByPresence extends AbstractTagFork {

    private final String tagName;
    private final static String TAGPRESENT = "present";
    private final static String TAGABSENT = "absent";

    public ForkTagByPresence(String tagName) {
        super();
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return String.format("Distribute samples by the presence of the given tag to forks '%s' and '%s'.", TAGPRESENT, TAGABSENT);
    }

    @Override
    public void setSubPipelines(Collection<Pair<String, PipelineBuilder>> subPipelines) throws IOException {
        for (Pair pair : subPipelines) {
            if (!pair.getLeft().equals(TAGPRESENT) && !pair.getLeft().equals(TAGABSENT)) {
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
