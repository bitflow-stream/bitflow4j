package bitflow4j.steps.tags;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Description;
import bitflow4j.script.registry.Optional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Description("Removes all tags of passed through samples or only the specified List of Tags.")
public class StripTags extends AbstractPipelineStep {

    private final List<String> tags;

    public StripTags(){
        this.tags = null;
    }

    @BitflowConstructor
    public StripTags(@Optional List<String> tags){
        this.tags = tags;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if(tags == null || tags.isEmpty()) {
            sample.setAllTags(new HashMap<>());
        }
        else{
            for (String tagName : tags) {
                if (sample.getTags().keySet().contains(tagName)) {
                    sample.getTags().remove(tagName);
                }
            }
        }
        output.writeSample(sample);
    }

}
