package metrics.io.fork;

import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 5/2/16.
 */
public class LabelFork extends AbstractFork<String> {

    private final String defaultOutput;

    public LabelFork(OutputStreamFactory<String> outputFactory, String defaultOutput) {
        super(outputFactory);
        this.defaultOutput = defaultOutput;
    }

    public LabelFork(String defaultOutput) {
        super();
        this.defaultOutput = defaultOutput;
    }

    public LabelFork() {
        super();
        this.defaultOutput = null;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        String label = sample.getLabel();
        MetricOutputStream output = getOutputStream(label);
        if (output == null && defaultOutput != null) {
            output = getOutputStream(defaultOutput);
        }
        if (output != null) {
            output.writeSample(sample);
        }
    }

}
