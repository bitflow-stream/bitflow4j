package bitflow4j.steps.batch;

import bitflow4j.PipelineStep;
import bitflow4j.Sample;
import bitflow4j.window.SampleWindow;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

/**
 * Takes in a batch of samples and outputs a reduced amount of them
 */
public class BatchDataReducer extends BatchPipelineStep{
    private SampleWindow samples;
    private int outputAmount;
    private Random r;
    public enum mode {
        UNIFORM, RANDOM, FIRST
    }
    private mode selectionMode;

    public BatchDataReducer(int outputAmount, mode selectionMode){
        this.outputAmount = outputAmount;
        samples = new SampleWindow();
        this.selectionMode = selectionMode;
        r = new Random();
    }

    /**
     * Creates pipeline step in random mode with the given seed
     */
    public BatchDataReducer(int outputAmount, long seed){
        this.outputAmount = outputAmount;
        samples = new SampleWindow();
        this.selectionMode = mode.RANDOM;
        r = new Random(seed);
    }

    @Override
    protected void flushAndClearResults() throws IOException {
        int amount = outputAmount > samples.samples.size() ? samples.samples.size() : outputAmount;
        PipelineStep.logger.log(Level.INFO, "Reducing samples from " + samples.samples.size() + " to " + amount);
        switch (selectionMode){
            case UNIFORM:
                double gap = samples.samples.size()/(double)amount;
                int n = 0;
                int nextSample = 0, sampleCount = 0;
                double preciseIdx = 0;
                for (Sample s : samples.samples){
                    if(n++ == nextSample){
                        preciseIdx += gap;
                        nextSample = (int)Math.round(preciseIdx);
                        sampleCount++;
                        output.writeSample(s);
                    }
                }
                break;
            case RANDOM:
                for(int i=0; i<amount; i++){
                    output.writeSample(samples.samples.remove(r.nextInt(samples.samples.size())));
                }
                break;
            case FIRST:
                int idx = 0;
                for(Sample s : samples.samples) {
                    if (idx++ == amount) break;
                    output.writeSample(s);
                }
        }
    }

    @Override
    protected void addSample(Sample sample) {
        samples.addSample(sample);
    }
}
