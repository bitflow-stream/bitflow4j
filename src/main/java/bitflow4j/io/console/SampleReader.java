package bitflow4j.io.console;

import bitflow4j.io.SampleInputStream;
import bitflow4j.io.ThreadedSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.io.InputStream;

public class SampleReader extends ThreadedSource {

    private final Marshaller marshaller;
    private final InputStream input;
    private final String name;

    public SampleReader(Marshaller marshaller) {
        this(System.in, marshaller, "stdin");
    }

    public SampleReader(InputStream input, Marshaller marshaller, String streamName) {
        this.marshaller = marshaller;
        this.input = input;
        this.name = streamName;
    }

    @Override
    public String toString() {
        return String.format("Reading from %s (format %s)", name, marshaller);
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        super.start(pool);
        readSamples(new SampleInputStream.Single(input, "Sample input stream reading from " + name, pool, marshaller));
        initFinished();
    }

}
