package bitflow4j.steps.metrics;

import bitflow4j.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anton on 08.02.17.
 */
public class MetricSorter extends AbstractMetricReordering {

    @Override
    protected int[] newHeaderIndices(Header inHeader) {
        List<StringIndexPair> fields = new ArrayList<>();
        for (int i = 0; i < inHeader.header.length; i++) {
            fields.add(new StringIndexPair(inHeader.header[i], i));
        }
        Collections.sort(fields);

        int indices[] = new int[inHeader.header.length];
        int i = 0;
        for (StringIndexPair field : fields) {
            indices[i++] = field.index;
        }
        return indices;
    }

    private static class StringIndexPair implements Comparable<StringIndexPair> {

        public final String string;
        public final int index;

        private StringIndexPair(String string, int index) {
            this.string = string;
            this.index = index;
        }

        @Override
        public int compareTo(StringIndexPair o) {
            return string.compareTo(o.string);
        }
    }

}
