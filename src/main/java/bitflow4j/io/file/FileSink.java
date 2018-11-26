package bitflow4j.io.file;

import bitflow4j.io.AbstractSampleOutput;
import bitflow4j.io.marshall.Marshaller;

import java.io.*;

/**
 * Created by anton on 4/16/16.
 * <p>
 * This PipelineStep starts a new file for every incoming header. This is better suited for writing files than SampleOutput, which will print every
 * incoming header into the same file. However, if the append flag is set to true, all headers and samples will be appended
 * to a single file, even if it already exists, which is essentially like using a SampleOutput directly.
 */
public class FileSink extends AbstractSampleOutput {

    private final boolean append;
    private final FileGroup files;
    private int index = 0;

    public FileSink(String baseFileName, Marshaller marshaller) throws IOException {
        this(baseFileName, marshaller, false);
    }

    public FileSink(String baseFileName, Marshaller marshaller, boolean append) throws IOException {
        super(marshaller);
        files = new FileGroup(baseFileName);
        this.append = append;
        if (!append)
            files.deleteFiles();
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        if (output != null) {
            output.flush();
            if (append) // Do not create new files for new headers
                return output;
        }
        String filename = files.getFile(index++);
        File file = new File(filename);

        boolean fileExists = (!append && file.createNewFile()) || (file.exists() || file.createNewFile());
        if (!fileExists) {
            throw new IOException("Failed to create file " + filename);
        }
        return new BufferedOutputStream(new FileOutputStream(file, append));
    }

}
