package bitflow4j.io.net;

import bitflow4j.io.ThreadedReaderSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by anton on 04.11.16.
 */
public class TcpSource extends ThreadedReaderSource {

    private final String[] tcpSources;
    private final Marshaller marshaller;

    public TcpSource(String[] tcpSources, Marshaller marshaller) {
        this.tcpSources = tcpSources;
        this.marshaller = marshaller;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        for (String source : tcpSources) {
            TcpSampleReader reader = new TcpSampleReader(source, pool, marshaller);
            readSamples(pool, reader, true);
        }
    }

    @Override
    public void run() throws IOException {
        // All readers have been added, so we can immediately start waiting for them to finish
        // Since the connections are continuously re-established, this should not actually happen.
        shutDown();
        super.run();
    }

    @Override
    public String toString() {
        return "TCP downloader: " + Arrays.toString(tcpSources);
    }

}
