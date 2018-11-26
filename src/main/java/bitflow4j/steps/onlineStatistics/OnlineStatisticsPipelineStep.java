package bitflow4j.steps.onlineStatistics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 09.02.17.
 */
public class OnlineStatisticsPipelineStep extends AbstractPipelineStep {

    private final OnlineStatisticsWindow window;

    public OnlineStatisticsPipelineStep(int window) {
        this(window, "input", "mean", "var"); // "var", "stddev", "slope", "relSlope", "meanSlope"
    }

    public OnlineStatisticsPipelineStep(int window, String... features) {
        this.window = new OnlineStatisticsWindow(window, features);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        super.writeSample(window.compute(sample));
    }

}
