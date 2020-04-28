package bitflow4j;

import java.io.IOException;
import java.util.logging.Logger;

public class Runner {

    private static final Logger logger = Logger.getLogger(Runner.class.getName());

    private volatile boolean running = true;

    public void run(ProcessingStep step, SampleChannel channel) throws IOException {
        logger.info(String.format("Initializing step %s", step));
        step.initialize(new ContextImpl(channel));

        logger.info("Starting to receive samples...");
        while (running) {
            Sample sample = channel.readSample();
            if (sample == null)
                break;
            step.handleSample(sample);
        }

        step.cleanup();
        channel.close();
    }

    public void close() {
        running = false;
    }

}
