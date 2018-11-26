package bitflow4j.steps.reorder;

import bitflow4j.steps.batch.WindowBatchPipelineStep;
import bitflow4j.window.AbstractSampleWindow;
import bitflow4j.window.SortedWindow;

import java.io.IOException;

/**
 * Created by anton on 5/12/16.
 * <p>
 * Read all samples from the input into memory and output them, sorted by their timestamp.
 */
public class TimestampSort extends WindowBatchPipelineStep {

    private final AbstractSampleWindow window;

    public TimestampSort(SortedWindow window) {
        this.window = window;
    }

    public TimestampSort(boolean changingHeaders) {
        this(new SortedWindow(changingHeaders));
    }

    @Override
    protected void flushResults() throws IOException {
        for (int i = 0; i < window.numSamples(); i++)
            output.writeSample(window.getSample(i));
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

}