package bitflow4j.io;

import bitflow4j.Sample;
import bitflow4j.io.marshall.InputStreamClosedException;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.io.marshall.UnmarshalledHeader;
import bitflow4j.task.TaskPool;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Reads Samples from a single InputStream instance until it is closed.
 * This does not implement Source, but can be used for implementations.
 */
public abstract class SampleInputStream implements ThreadedSource.SampleGenerator {

    private static final Logger logger = Logger.getLogger(SampleInputStream.class.getName());

    private static final String BITFLOW_SOURCE_TAG = "bitflow-source";

    private final Marshaller marshaller;
    private final boolean addSourceTag;
    private boolean closed = false;

    private String sourceName;
    private InputStream currentInput;
    private UnmarshalledHeader header = null;

    public static Level headerUpdateLogLevel = Level.FINE;

    public SampleInputStream(Marshaller marshaller) {
        this.marshaller = marshaller;
        this.addSourceTag = false;
    }

    public SampleInputStream(Marshaller marshaller, boolean addSourceTag) {
        this.marshaller = marshaller;
        this.addSourceTag = addSourceTag;
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
    public Sample nextSample() throws IOException {
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
                logger.log(headerUpdateLogLevel, String.format("Incoming header of '%s' updated to %s metrics", sourceName, header.header.numFields()));
                return null;
            } else {
                if (header == null) {
                    throw new IOException("Input stream '" + sourceName + "' contains Sample before first Header");
                }
                Sample s = marshaller.unmarshallSample(input, header);
                if(addSourceTag)
                    s.setTag(BITFLOW_SOURCE_TAG, sourceName);
                return s;
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
            logger.log(headerUpdateLogLevel, String.format("Closed input %s", sourceName));
            input.close();
        }
    }

    public static class Single extends SampleInputStream {

        boolean returned = false;
        private final String name;
        private final InputStream input;

        public Single(InputStream input, String name, TaskPool pool, Marshaller marshaller) {
            super(marshaller);
            this.input = input;
            this.name = name;
        }

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

    }

}
