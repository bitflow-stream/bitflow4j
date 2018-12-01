package bitflow4j.io.file;

import bitflow4j.io.MarshallingSampleWriter;
import bitflow4j.io.marshall.Marshaller;

import java.io.*;

/**
 * Created by anton on 4/16/16.
 * <p>
 * This PipelineStep starts a new file for every incoming header. This is better suited for writing files than SampleWriter, which will print every
 * incoming header into the same file. However, if the append flag is set to true, all headers and samples will be appended
 * to a single file, even if it already exists, which is essentially like using a SampleWriter directly.
 */
public class FileSink extends MarshallingSampleWriter {

    private final boolean append;
    private final boolean deleteFiles;
    private final FileGroup files;
    private int index = 0;

    public FileSink(String baseFileName, Marshaller marshaller) throws IOException {
        this(baseFileName, marshaller, false, false);
    }

    public FileSink(String baseFileName, Marshaller marshaller, boolean append, boolean deleteFiles) throws IOException {
        super(marshaller);
        files = new FileGroup(baseFileName);
        this.append = append;
        this.deleteFiles = deleteFiles;
        if (deleteFiles)
            files.deleteFiles();
    }

    @Override
    public String toString() {
        return String.format("Writing file %s (append: %s, deleteFiles: %s, format: %s)", files, append, deleteFiles, getMarshaller());
    }

    @Override
    protected OutputStream nextOutputStream() throws IOException {
        if (output != null && append) {
            // Do not create new files for new headers
            return output;
        }
        File file;
        do {
            file = new File(files.getFile(index++));
        } while (!append && file.exists());

        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create file " + file);
        }
        return new BufferedOutputStream(new FileOutputStream(file, append));
    }

}
