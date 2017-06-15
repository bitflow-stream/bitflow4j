package bitflow4j.io;

import bitflow4j.io.marshall.Marshaller;

import java.io.*;

/**
 * Simple SampleWriter writing to a single fixed OutputStream.
 * <p>
 * Created by anton on 4/7/16.
 */
public class SampleWriter extends AbstractSampleWriter {

    private final OutputStream output;

    public SampleWriter(Marshaller marshaller) {
        this(System.out, marshaller);
    }

    public SampleWriter(String filename, Marshaller marshaller) throws FileNotFoundException {
        this(new FileOutputStream(filename, false), marshaller);
    }

    public SampleWriter(File file, Marshaller marshaller) throws FileNotFoundException {
        this(new FileOutputStream(file, false), marshaller);
    }

    public SampleWriter(OutputStream output, Marshaller marshaller) {
        super(marshaller);
        this.output = output;
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        return output;
    }

}
