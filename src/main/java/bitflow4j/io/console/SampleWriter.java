package bitflow4j.io.console;

import bitflow4j.io.MarshallingSampleWriter;
import bitflow4j.io.marshall.Marshaller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Simple SampleWriter writing to a single fixed OutputStream.
 * <p>
 * Created by anton on 4/7/16.
 */
public class SampleWriter extends MarshallingSampleWriter {

    private final OutputStream output;
    private final String name;

    public SampleWriter(Marshaller marshaller) {
        this(System.out, marshaller, "stdout");
    }

    public SampleWriter(String filename, Marshaller marshaller) throws FileNotFoundException {
        this(new File(filename), marshaller);
    }

    public SampleWriter(File file, Marshaller marshaller) throws FileNotFoundException {
        this(file, marshaller, false);
    }

    public SampleWriter(File file, Marshaller marshaller, boolean append) throws FileNotFoundException {
        this(new FileOutputStream(file, append), marshaller, String.format("%sfile: %s", (append ? "appending to" : ""), file));
    }

    public SampleWriter(OutputStream output, Marshaller marshaller, String streamName) {
        super(marshaller);
        this.output = output;
        this.name = streamName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected OutputStream nextOutputStream() {
        return output;
    }

}
