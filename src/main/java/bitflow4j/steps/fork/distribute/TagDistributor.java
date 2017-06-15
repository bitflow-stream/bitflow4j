package bitflow4j.steps.fork.distribute;

import bitflow4j.steps.fork.Distributor;
import bitflow4j.sample.Sample;

/**
 * Created by anton on 5/2/16.
 * <p>
 * The TagDistributor distributes incoming Samples based on tag values stored in the samples.
 * The default functionality is to use the value of one given tag as the sub-pipeline key, but
 * multiple tags can also be defined. In that case the values will be concatenated, and a separator string
 * will be added between the individual values.
 */
public class TagDistributor implements Distributor {

    private final String tagNames[];
    private final String separator;

    public TagDistributor(String tagName) {
        this("", tagName);
    }

    public TagDistributor(String separator, String... tagNames) {
        this.tagNames = tagNames;
        this.separator = separator;
    }

    @Override
    public Object[] distribute(Sample sample) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tagNames.length; i++) {
            if (i > 0)
                builder.append(separator);
            builder.append(sample.getTag(tagNames[i]));
        }
        return new Object[]{builder.toString()};
    }
}
