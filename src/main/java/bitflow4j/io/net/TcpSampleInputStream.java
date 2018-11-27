package bitflow4j.io.net;

import bitflow4j.Sample;
import bitflow4j.io.SampleInputStream;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 04.11.16.
 */
public class TcpSampleInputStream extends SampleInputStream {

    private static final Logger logger = Logger.getLogger(TcpSampleInputStream.class.getName());

    private final TaskPool pool;
    private final String host;
    private final int port;
    private final String sourceName;
    public static final long retryTimeoutMillis = 1000;

    public TcpSampleInputStream(String tcpSource, Marshaller marshaller, TaskPool pool) throws IOException {
        this(TcpSink.getHost(tcpSource), TcpSink.getPort(tcpSource), marshaller, pool);
    }

    public TcpSampleInputStream(String host, int port, Marshaller marshaller, TaskPool pool) throws IOException {
        super(marshaller);
        this.host = host;
        this.port = port;
        this.pool = pool;
        sourceName = host + ":" + port;
        logger.log(Level.INFO, "Polling samples from {0}", sourceName);
    }

    @Override
    public String toString() {
        return "Polling TCP samples from " + sourceName;
    }

    private static class OpenFailedException extends IOException {
        private final IOException cause;

        OpenFailedException(IOException e) {
            cause = e;
        }
    }

    @Override
    protected NamedInputStream nextInput() throws IOException {
        try {
            InputStream stream = new Socket(host, port).getInputStream();
            return new NamedInputStream(stream, sourceName);
        } catch (IOException e) {
            throw new OpenFailedException(e);
        }
    }

    @Override
    public Sample nextSample() throws IOException {
        while (true) {
            try {
                return super.nextSample();
            } catch (OpenFailedException e) {
                TcpErrorLogger.log("Failed to establish output connection to " + sourceName, e);
                if (!pool.sleep(retryTimeoutMillis))
                    return null;
            }
        }
    }

}
