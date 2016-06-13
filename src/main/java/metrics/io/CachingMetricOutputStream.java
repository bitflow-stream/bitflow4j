package metrics.io;

import metrics.Marshaller;
import metrics.Sample;
import metrics.io.file.FileMetricReader;
import metrics.main.AlgorithmPipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by anton on 5/3/16.
 * <p>
 * An wrapper for another OutputStream. Checks whether a cache file exists and if so, offers to read the file
 * into the wrapped stream. If this stream is used (and there was a cache miss), all data is forwarded to the wrapped
 * stream and additionally written into the cache file.
 */
public class CachingMetricOutputStream extends AbstractOutputStream {

    private static final Marshaller CacheMarshaller = AlgorithmPipeline.getMarshaller("CSV");

    private final FileMetricReader reader = new FileMetricReader(CacheMarshaller, FileMetricReader.FILE_NAME);
    private final MetricOutputStream wrappedStream;
    private MetricPrinter cacheWriter = null;

    public CachingMetricOutputStream(File cacheFile, MetricOutputStream wrappedStream) throws FileNotFoundException {
        this.wrappedStream = wrappedStream;
        try {
            reader.addFile(cacheFile);
        } catch (IOException exc) {
            // Cache miss
            cacheWriter = new MetricPrinter(cacheFile, CacheMarshaller);
        }
    }

    public boolean isCacheHit() {
        return cacheWriter == null;
    }

    public FileMetricReader getCacheReader() {
        return reader;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        wrappedStream.writeSample(sample);
        if (cacheWriter != null)
            cacheWriter.writeSample(sample);
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        wrappedStream.close();
    }

    @Override
    public synchronized void waitUntilClosed() {
        super.waitUntilClosed();
        wrappedStream.waitUntilClosed();
    }
}
