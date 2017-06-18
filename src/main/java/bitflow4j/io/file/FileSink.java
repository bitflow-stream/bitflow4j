package bitflow4j.io.file;

import bitflow4j.io.AbstractSampleWriter;
import bitflow4j.io.marshall.Marshaller;

import java.io.*;

/**
 * Created by anton on 4/16/16.
 * <p>
 * This Sink starts a new file for every incoming header.
 * This is better suited for writing files than SampleWriter, which will print every
 * incoming header into the same file.
 */
public class FileSink extends AbstractSampleWriter {

    private final FileGroup files;
    private int index = 0;

    public FileSink(String baseFileName, Marshaller marshaller) throws IOException {
        super(marshaller);
        files = new FileGroup(baseFileName);
        files.deleteFiles();
    }
    
    public FileSink(String baseFileName, Marshaller marshaller, boolean extendFile) throws IOException {
        super(marshaller, extendFile);
        files = new FileGroup(baseFileName);
        files.deleteFiles();
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        String filename = files.getFile(index++);
        File file = new File(filename);
        if (!file.createNewFile())
            throw new IOException("Failed to create file " + filename);
        return new BufferedOutputStream(new FileOutputStream(file));
    }

}
