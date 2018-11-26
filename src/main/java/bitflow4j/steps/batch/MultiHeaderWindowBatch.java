package bitflow4j.steps.batch;

import bitflow4j.window.AbstractSampleWindow;
import bitflow4j.window.MetricWindow;
import bitflow4j.window.MetricWindowFactory;
import bitflow4j.window.MultiHeaderWindow;

import java.io.IOException;

/**
 * This class extends the basic WindowBatchPipelineStep and provides an implementation using a {@link MultiHeaderWindow} for underlying samples.
 */
public class MultiHeaderWindowBatch extends WindowBatchPipelineStep {

    protected MultiHeaderWindow window;

    public MultiHeaderWindowBatch() {
        this(null);
    }

    public MultiHeaderWindowBatch(MetricWindowFactory factory) {
        window = new MultiHeaderWindow<MetricWindow>(factory == null ? MetricWindow.FACTORY : factory);
    }

    /**
     * The basic pipeline step will use the MultiHeaderWindow to adjust all sample headers.
     * This method will only be called when the underlying stream is closed (batch mode).
     * Sub-classes should override this method.
     *
     * @throws IOException if any error occurs
     */
    @Override
    protected synchronized void flushResults() throws IOException {
        int numSamples = window.numSamples();
        for (int i = 0; i < numSamples; i++) {
            this.output.writeSample(window.makeSample(i));
        }
    }

    @Override
    public synchronized void doClose() throws IOException {
        flushResults();
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

}