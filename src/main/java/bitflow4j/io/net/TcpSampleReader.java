package bitflow4j.io.net;

import bitflow4j.io.SampleReader;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.sample.Sample;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by anton on 04.11.16.
 */
public class TcpSampleReader extends SampleReader {

    private static final Logger logger = Logger.getLogger(TcpSampleReader.class.getName());

    private final String host;
    private final int port;
    private final String sourceName;
    public static final long retryTimeoutMillis = 1000;

    public TcpSampleReader(String tcpSource, TaskPool pool, Marshaller marshaller) throws IOException {
        super(pool, marshaller);
        try {
            URI uri = new URI("protocol://" + tcpSource);
            host = uri.getHost();
            port = uri.getPort();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        sourceName = host + ":" + port;
        logger.info("Polling samples from " + sourceName);
    }

    @Override
    public String toString() {
        return "Polling samples from " + sourceName;
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
                logger.fine("Failed to establish TCP connection to " + sourceName + ": " + e.cause);
                if (!pool.sleep(retryTimeoutMillis))
                    return null;
            }
        }
    }

}
