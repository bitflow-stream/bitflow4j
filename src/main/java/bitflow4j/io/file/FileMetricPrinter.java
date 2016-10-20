package bitflow4j.io.file;

import bitflow4j.Marshaller;
import bitflow4j.io.AbstractMetricPrinter;

import java.io.*;

/**
 * Created by anton on 4/16/16.
 * <p>
 * This MetricOutputStream starts a new file for every incoming header.
 * This is better suited for writing files than MetricPrinter, which will print every
 * incoming header into the same file.
 */
public class FileMetricPrinter extends AbstractMetricPrinter {

    private final FileGroup files;
    private int index = 0;

    public FileMetricPrinter(String baseFileName, Marshaller marshaller) throws IOException {
        super(marshaller);
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
