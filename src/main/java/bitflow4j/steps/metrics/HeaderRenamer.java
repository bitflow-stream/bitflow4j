package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rename of headers fitting a given regex pattern and replaced by specific name.
 *
 * @author fschmidt
 */
public class HeaderRenamer extends AbstractPipelineStep {

    private final List<Pattern> patterns;
    private final List<String> replacements;
    private Header inHeader;
    private Header outHeader;

    private final String loggingName;

    public HeaderRenamer(String loggingName, List<Pattern> patterns, List<String> replacements) {
        if (patterns.size() != replacements.size()) {
            throw new IllegalArgumentException("Patterns and replacements must have equal size: " + patterns.size() + " != " + replacements.size());
        }
        this.loggingName = loggingName;
        this.patterns = patterns;
        this.replacements = replacements;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (sample.headerChanged(inHeader)) {
            int changes = 0;
            inHeader = sample.getHeader();
            String outFields[] = new String[inHeader.header.length];
            for (int i = 0; i < inHeader.header.length; i++) {
                String currentName = inHeader.header[i];
                boolean matched = false;
                for (int j = 0; j < patterns.size(); j++) {
                    Matcher matcher = patterns.get(j).matcher(currentName);
                    String before = currentName;
                    currentName = matcher.replaceAll(replacements.get(j));

                    // TODO this is inefficient, find a better way to check if replaceAll() changed the input
                    if (!currentName.equals(before)) {
                        matched = true;
                    }
                }
                if (matched) changes++;
                outFields[i] = currentName;
            }
            outHeader = new Header(outFields);
            logger.info(this + ": Changed " + changes + " out of " + inHeader.header.length + " metrics");
            logger.fine(this + ": From header: " + Arrays.toString(inHeader.header));
            logger.fine(this + ":   To header: " + Arrays.toString(outFields));
        }
        output.writeSample(new Sample(outHeader, sample.getMetrics(), sample));
    }

    public String toString() {
        return "RenameHeader " + loggingName;
    }

}
