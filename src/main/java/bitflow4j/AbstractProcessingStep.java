package bitflow4j;

import java.io.IOException;

public abstract class AbstractProcessingStep implements ProcessingStep {

    protected Context context = null;

    @Override
    public void initialize(Context context) throws IOException {
        this.context = context;
    }

    @Override
    public void handleSample(Sample sample) throws IOException {
    }

    @Override
    public void cleanup() throws IOException {
    }

    public void output(Sample sample) throws IOException {
        this.context.outputSample(sample);
    }

}
