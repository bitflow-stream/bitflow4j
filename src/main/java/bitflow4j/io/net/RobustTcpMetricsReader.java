package bitflow4j.io.net;

import bitflow4j.Marshaller;
import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricReader;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by anton on 04.11.16.
 */
public class RobustTcpMetricsReader implements MetricInputStream {

    private static final Logger logger = Logger.getLogger(RobustTcpMetricsReader.class.getName());

    private final Marshaller marshaller;
    private final String host;
    private final int port;
    private MetricReader currentReader;
    private Socket currentSocket;
    public long retryTimeoutMillis = 1000;

    public RobustTcpMetricsReader(String tcpSource, Marshaller marshaller) throws URISyntaxException {
        URI uri = new URI("protocol://" + tcpSource);
        this.marshaller = marshaller;
        host = uri.getHost();
        port = uri.getPort();
        logger.info("Polling samples from " + tcpSource);
    }

    public RobustTcpMetricsReader(String host, int port, Marshaller marshaller) {
        this.marshaller = marshaller;
        this.host = host;
        this.port = port;
    }

    @Override
    public Sample readSample() throws IOException {
        while (true) {
            if (currentReader == null) {
                try {
                    currentSocket = new Socket(host, port);
                    currentReader = new MetricReader(currentSocket.getInputStream(), getSource(), marshaller);
                } catch (IOException e) {
                    logger.fine("Failed to establish TCP connection to " + getSource() + ": " + e);
                    closeSocket();
                }
            }
            if (currentReader != null) {
                try {
                    return currentReader.readSample();
                } catch (InputStreamClosedException e) {
                    // This stream is never closed, continue polling the data source forever.
                    logger.info("Connection with " + getSource() + " closed.");
                    closeSocket();
                } catch (IOException e) {
                    logger.fine("Error reading from " + getSource() + ": " + e);
                    closeSocket();
                }
            }
            try {
                Thread.sleep(retryTimeoutMillis);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    private void closeSocket() {
        if (currentSocket != null) {
            try {
                currentSocket.close();
            } catch (IOException ex) {
                logger.fine("Error closing socket with " + getSource() + ": " + ex);
            }
            currentSocket = null;
        }
        currentReader = null;
    }

    public String getSource() {
        return host + ":" + port;
    }

}
