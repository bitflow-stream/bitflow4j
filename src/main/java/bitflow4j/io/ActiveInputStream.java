package bitflow4j.io;

import bitflow4j.Sample;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for implementing MetricInputStream in case multiple threads
 * are needed, for example when reading from TCP connections.
 * <p>
 * Created by anton on 23.12.16.
 */
public abstract class ActiveInputStream implements MetricInputStream {

    private static final Logger logger = Logger.getLogger(ActiveInputStream.class.getName());

    public static final int PIPE_BUFFER = 128;

    protected MetricPipe pipe = new MetricPipe(PIPE_BUFFER);

    @Override
    public Sample readSample() throws IOException {
        return pipe.readSample();
    }

    protected class ReaderThread extends Thread {

        private final MetricInputStream input;
        private final String name;

        public ReaderThread(String name, MetricInputStream input) {
            this.input = input;
            this.name = name;
        }

        public void run() {
            while (true) {
                try {
                    Sample sample = input.readSample();
                    pipe.writeSample(sample);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error reading from " + name, e);
                    return;
                }
            }
        }

    }

}
