package metrics.io;

import metrics.Marshaller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        return new FileOutputStream(files.getFile(index++));
    }

}
