package bitflow4j.io;

import bitflow4j.io.marshall.Marshaller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Simple SampleOutput writing to a single fixed OutputStream.
 * <p>
 * Created by anton on 4/7/16.
 */
public class SampleOutput extends AbstractSampleOutput {

    private final OutputStream output;

    public SampleOutput(Marshaller marshaller) {
        this(System.out, marshaller);
    }

    public SampleOutput(String filename, Marshaller marshaller) throws FileNotFoundException {
        this(new File(filename), marshaller);
    }

    public SampleOutput(File file, Marshaller marshaller) throws FileNotFoundException {
        this(file, marshaller, false);
    }

    public SampleOutput(File file, Marshaller marshaller, boolean append) throws FileNotFoundException {
        this(new FileOutputStream(file, append), marshaller);
    }

    public SampleOutput(OutputStream output, Marshaller marshaller) {
        super(marshaller);
        this.output = output;
    }

    @Override
    protected OutputStream nextOutputStream() {
        return output;
    }

}
