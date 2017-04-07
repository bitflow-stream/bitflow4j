package bitflow4j.io;

import bitflow4j.io.marshall.InputStreamClosedException;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.io.marshall.UnmarshalledHeader;
import bitflow4j.sample.Sample;
import bitflow4j.task.TaskPool;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Reads Samples from a single InputStream instance until it is closed.
 * This does not implement SampleSource, but can be used for implementations.
 */
public abstract class MetricReader {

    private static final Logger logger = Logger.getLogger(MetricReader.class.getName());

    protected final TaskPool pool;
    private final Marshaller marshaller;
    private boolean closed = false;

    private String sourceName;
    private InputStream currentInput;
    private UnmarshalledHeader header = null;

    // TODO HACK should be removed, see usages of this
    public Runnable inputClosedHook;

    public boolean suppressHeaderUpdateLogs = false;

    public MetricReader(TaskPool pool, Marshaller marshaller) {
        this.marshaller = marshaller;
        this.pool = pool;
    }

    public static MetricReader singleInput(TaskPool pool, Marshaller marshaller, String name, InputStream input) {
        return new MetricReader(pool, marshaller) {
            boolean returned = false;

            @Override
            public String toString() {
                return name;
            }

            @Override
            protected NamedInputStream nextInput() throws IOException {
                if (returned) return null;
                returned = true;
                return new NamedInputStream(input, name);
            }
        };
    }

    public static class NamedInputStream {
        public final InputStream stream;
        public final String name;

        public NamedInputStream(InputStream stream, String name) {
            this.stream = stream;
            this.name = name;
        }
    }

    protected abstract NamedInputStream nextInput() throws IOException;

    // Returning null here means all inputs were finished without error
    public Sample readSample() throws IOException {
        Sample sample = null;
        while (!closed && (sample = doReadSample()) == null) ;
        return sample;
    }

    private Sample doReadSample() throws IOException {
        InputStream input;
        // Make sure that after this synchronized block, currentInput and input point to the same object
        synchronized (this) {
            if (closed) {
                return null;
            }
            input = currentInput;
            if (input == null) {
                NamedInputStream named = nextInput();
                if (named == null) {
                    // No more inputs
                    close();
                    return null;
                }
                input = new BufferedInputStream(named.stream);
                currentInput = input;
                sourceName = named.name;
            }
        }

        try {
            boolean isHeader = marshaller.peekIsHeader(input);
            if (isHeader) {
                header = marshaller.unmarshallHeader(input);
                if (!suppressHeaderUpdateLogs)
                    logger.info("Incoming header of '" + sourceName + "' updated to " + header.header.numFields() + " metrics");
                return null;
            } else {
                if (header == null) {
                    throw new IOException("Input stream '" + sourceName + "' contains Sample before first Header");
                }
                Sample sample = marshaller.unmarshallSample(input, header);
                sample.setSource(sourceName);
                return sample;
            }
        } catch (IOException e) {
            closeCurrentInput();
            if (closed || e instanceof InputStreamClosedException) {
                // If stream is closed on purpose from the outside, ignore the exception
                return null;
            } else {
                // In case of errors, the caller will have to explicitly close this reader
                throw e;
            }
        }
    }

    public synchronized void close() throws IOException {
        closed = true;
        closeCurrentInput();
    }

    private synchronized void closeCurrentInput() throws IOException {
        if (currentInput != null) {
            InputStream input = currentInput;
            currentInput = null;
            if (!suppressHeaderUpdateLogs)
                logger.info("Closed input " + sourceName);
            Runnable hook = inputClosedHook;
            if (hook != null) {
                hook.run();
            }
            input.close();
        }
    }

}
