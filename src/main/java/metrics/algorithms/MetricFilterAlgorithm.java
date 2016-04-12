package metrics.algorithms;

import metrics.Sample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 4/11/16.
 *
 * Only pass out a single metric identified by column index.
 */
public class MetricFilterAlgorithm extends GenericAlgorithm {

    private final Set<Integer> columns = new HashSet<>();
    private final int maxCol;

    // col should be sorted
    public MetricFilterAlgorithm(int ...col) {
        super();
        for (int i : col)
            this.columns.add(i);
        this.maxCol = col.length == 0 ? 0 : col[col.length - 1];
    }

    @Override
    public String toString() {
        return "metric filter " + columns.toString();
    }

    protected Sample executeSample(Sample sample) throws IOException {
        String allFields[] = sample.getHeader().header;
        String fields[] = new String[columns.size()];
        double values[] = new double[columns.size()];
        int j = 0;
        for (int i = 0; i < allFields.length && i <= maxCol; i++) {
            if (columns.contains(i)) {
                fields[j] = allFields[i];
                values[j] = sample.getMetrics()[i];
                j++;
            }
        }
        if (j < fields.length) {
            String all[] = fields;
            fields = new String[j];
            System.arraycopy(all, 0, fields, 0, j);
            double allD[] = values;
            values = new double[j];
            System.arraycopy(allD, 0, values, 0, j);
        }
        Sample.Header header = new Sample.Header(sample.getHeader(), fields);
        return new Sample(header, values, sample.getTimestamp(), sample.getSource(), sample.getLabel());
    }

}
