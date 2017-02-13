package bitflow4j.io.net;

import bitflow4j.io.MetricReader;
import bitflow4j.io.ThreadedSampleSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 04.11.16.
 */
public class TcpMetricsDownloader extends ThreadedSampleSource {

    private final String[] tcpSources;
    private final Marshaller marshaller;
    private final List<TcpMetricsReader> readers;

    public TcpMetricsDownloader(String[] tcpSources, Marshaller marshaller) {
        this.tcpSources = tcpSources;
        this.marshaller = marshaller;
        this.readers = new ArrayList<>(tcpSources.length);
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        for (int i = 0; i < tcpSources.length; i++) {
            String source = tcpSources[i];
            MetricReader reader = new TcpMetricsReader(source, pool, marshaller);
            readSamples(pool, source, readers.get(i));
        }

        // All readers have been added, so we can immediately start waiting for them to finish
        // Since the connection is continuously re-established, this should not actually happen.
        shutDown();
        super.start(pool);
    }

}
