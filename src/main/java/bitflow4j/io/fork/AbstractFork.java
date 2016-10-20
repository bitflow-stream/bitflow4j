package bitflow4j.io.fork;

import bitflow4j.io.AbstractOutputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.main.ParameterHash;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/28/16.
 * <p>
 * Incoming Samples are distributed into multiple subsequent output streams.
 * Closing this output stream automatically closes all subsequent output streams.
 */
public abstract class AbstractFork<T> extends AbstractOutputStream {

    private final Set<MetricOutputStream> outputSet = new HashSet<>();
    private final Map<T, MetricOutputStream> outputs = new HashMap<>();
    private OutputStreamFactory<T> outputFactory;

    AbstractFork(OutputStreamFactory<T> outputFactory) {
        this.outputFactory = outputFactory;
    }

    // The factory must be set through setOutputFactory() before first writeSample() invocation
    AbstractFork() {
        this(null);
    }

    @Override
    public void close() throws IOException {
        super.close();

        // Close subsequent outputs
        List<IOException> errors = new ArrayList<>();
        for (MetricOutputStream output : outputSet) {
            try {
                output.close();
            } catch (IOException exc) {
                errors.add(exc);
            }
        }
        if (!errors.isEmpty()) {
            String msg;
            if (errors.size() == 1) {
                msg = "Error closing one output stream";
            } else {
                msg = errors.size() + " errors closing output streams.";
            }
            throw new IOException(msg, errors.get(0));
        }
    }

    @Override
    public void waitUntilClosed() {
        super.waitUntilClosed();
        outputSet.forEach(MetricOutputStream::waitUntilClosed);
    }

    public synchronized void setOutputFactory(OutputStreamFactory<T> factory) {
        if (this.outputFactory != null) {
            throw new IllegalStateException("OutputStreamFactory already has been initialized");
        }
        this.outputFactory = factory;
    }

    public MetricOutputStream getOutputStream(T key) throws IOException {
        if (outputFactory == null) {
            throw new IllegalStateException("OutputStreamFactory has not yet been initialized");
        }
        MetricOutputStream result = outputs.get(key);
        if (result == null) {
            synchronized (this) {
                result = outputs.get(key);
                if (result == null) {
                    result = outputFactory.getOutputStream(key);
                    outputSet.add(result);
                    outputs.put(key, result);
                }
            }
        }
        return result;
    }

    public void hashParameters(ParameterHash hash) {
        hash.writeClassName(this);
        hash.writeClassName(outputFactory);
    }

}
