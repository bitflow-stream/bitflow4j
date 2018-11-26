package bitflow4j.steps.filter;

import bitflow4j.Sample;

import java.util.List;

/**
 * @author fschmidt
 */
public class SampleTagFilter implements SampleFilter.Filter {

    public enum Operation {
        EXCLUDE_ALL, EXCLUDE_ANY, INCLUDE_ALL, INCLUDE_ANY
    }

    private final List<String> tags;
    private final Operation operation;

    public SampleTagFilter(List<String> tags, Operation operation) {
        this.tags = tags;
        this.operation = operation;
    }

    @Override
    public boolean shouldInclude(Sample sample) {
        switch (operation) {
            case INCLUDE_ANY:
                for (String tag : tags) {
                    if (sample.hasTag(tag)) {
                        return true;
                    }
                }
                return false;
            case INCLUDE_ALL:
                boolean hasAllTags = true;
                for (String tag : tags) {
                    if (!sample.hasTag(tag)) {
                        hasAllTags = false;
                        break;
                    }
                }
                return hasAllTags;
            case EXCLUDE_ANY:
                for (String tag : tags) {
                    if (sample.hasTag(tag)) {
                        return false;
                    }
                }
                return true;
            case EXCLUDE_ALL:
                boolean hasAllTagsToBeRemoved = true;
                for (String tag : tags) {
                    if (!sample.hasTag(tag)) {
                        hasAllTagsToBeRemoved = false;
                        break;
                    }
                }
                return !hasAllTagsToBeRemoved;
            default:
                throw new UnsupportedOperationException("Should not happen!");
        }
    }

}
