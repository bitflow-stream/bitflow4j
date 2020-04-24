package bitflow4j;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class AbstractProcessingStep implements ProcessingStep {

    protected static final Logger logger = Logger.getLogger(AbstractProcessingStep.class.getName());

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
