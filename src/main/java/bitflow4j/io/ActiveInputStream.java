package bitflow4j.io;

import bitflow4j.Sample;
import bitflow4j.io.net.TcpMetricsReader;
import bitflow4j.main.TaskPool;

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

    protected void readSamples(TaskPool pool, String name, MetricInputStream input) {
        pool.start(name, new SampleReader(name, input));
    }

    private class SampleReader implements TaskPool.InterruptibleRunnable {

        private final MetricInputStream input;
        private final String name;

        public SampleReader(String name, MetricInputStream input) {
            this.input = input;
            this.name = name;
        }

        public void run(TaskPool.Wait wait) {
            // TODO Kind of a hack
            if (input instanceof TcpMetricsReader) {
                ((TcpMetricsReader) input).useTaskPool(wait);
            }
            while (wait.running()) {
                try {
                    Sample sample = input.readSample();
                    if (wait.running())
                        pipe.writeSample(sample);
                } catch (InputStreamClosedException e) {
                    logger.info("Input " + name + " closed");
                    return;
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error reading from " + name, e);
                    return;
                }
            }
        }

    }

}
