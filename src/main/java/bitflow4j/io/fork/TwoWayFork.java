package bitflow4j.io.fork;

import bitflow4j.Sample;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.main.ParameterHash;

import java.io.IOException;
import java.util.Random;

/**
 * Created by anton on 4/23/16.
 */
public class TwoWayFork extends AbstractFork<TwoWayFork.ForkType> {

    private final float redirectedPortion;
    private final Random rnd = new Random();

    public enum ForkType {
        Primary, Secondary
    }

    /**
     * Randomly distribute incoming samples between the two output streams.
     * The given portion goes into output1.
     * If redirectedPortion is 0, copy all incoming Samples to both output streams.
     */
    public TwoWayFork(float redirectedPortion, OutputStreamFactory<ForkType> outputs) {
        super(outputs);
        if (redirectedPortion < 0 || redirectedPortion > 1) {
            throw new IllegalArgumentException("redirectedPortion must be in 0..1: " + redirectedPortion);
        }
        this.redirectedPortion = redirectedPortion;
    }

    public TwoWayFork(float redirectedPortion) {
        this(redirectedPortion, null);
    }

    /**
     * Every incoming sample will be copied to both output streams.
     */
    public TwoWayFork(OutputStreamFactory<ForkType> outputs) {
        this(0, outputs);
    }

    public TwoWayFork() {
        this(null);
    }

    public TwoWayFork(MetricOutputStream output1, MetricOutputStream output2) {
        this(null);
        setOutputs(output1, output2);
    }

    public void setOutputs(MetricOutputStream output1, MetricOutputStream output2) {
        setOutputFactory((type) -> type == ForkType.Primary ? output1 : output2);
    }

    public void writeSample(Sample sample) throws IOException {
        if (redirectedPortion == 0) {
            getOutputStream(ForkType.Primary).writeSample(sample);
            getOutputStream(ForkType.Secondary).writeSample(sample);
        } else {
            if (rnd.nextFloat() < redirectedPortion) {
                getOutputStream(ForkType.Primary).writeSample(sample);
            } else {
                getOutputStream(ForkType.Secondary).writeSample(sample);
            }
        }
    }

}
