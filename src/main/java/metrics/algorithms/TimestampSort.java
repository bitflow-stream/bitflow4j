package metrics.algorithms;

import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SortedWindow;

import java.io.IOException;

/**
 * Created by anton on 5/12/16.
 * <p>
 * Read all samples from the input into memory and output them, sorted by their timestamp.
 */
public class TimestampSort extends WindowBatchAlgorithm {

    private final AbstractSampleWindow window;

    public TimestampSort(SortedWindow window) {
        this.window = window;
    }

    public TimestampSort(boolean changingHeaders) {
        this(new SortedWindow(changingHeaders));
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        for (int i = 0; i < window.numSamples(); i++)
            output.writeSample(window.getSample(i));
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

}
