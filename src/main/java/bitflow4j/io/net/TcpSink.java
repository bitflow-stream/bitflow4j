package bitflow4j.io.net;

import bitflow4j.Sample;
import bitflow4j.io.MarshallingSampleWriter;
import bitflow4j.io.marshall.Marshaller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 6/17/16.
 */
public class TcpSink extends MarshallingSampleWriter {

    private static final Logger logger = Logger.getLogger(TcpSink.class.getName());

    public static final int TCP_CONNECT_TIMEOUT_MILLIS = 3000;

    private final String targetHost;
    private final int targetPort;
    private Socket socket = null;

    public TcpSink(Marshaller marshaller, String endpoint) throws MalformedURLException {
        this(marshaller, getHost(endpoint), getPort(endpoint));
    }

    public TcpSink(Marshaller marshaller, String targetHost, int targetPort) {
        super(marshaller);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    @Override
    public String toString() {
        return String.format("Send samples to %s:%s (format %s)", targetHost, targetPort, getMarshaller());
    }

    public static String getHost(String tcpEndpoint) throws MalformedURLException {
        URL url = new URL("http://" + tcpEndpoint); // Exception when the tcp endpoint format is wrong.
        return url.getHost();
    }

    public static int getPort(String tcpEndpoint) throws MalformedURLException {
        URL url = new URL("http://" + tcpEndpoint); // Exception when the tcp endpoint format is wrong.
        return url.getPort();
    }

    public synchronized void writeSample(Sample sample) throws IOException {
        try {
            super.writeSample(sample);
        } catch (IOException exc) {
            TcpErrorLogger.log(String.format("Failed to send sample to %s:%s", targetHost, targetPort), exc);
            closeSocket();
        }
    }

    protected OutputStream nextOutputStream() {
        try {
            closeSocket();
            socket = new Socket();
            socket.connect(new InetSocketAddress(targetHost, targetPort), TCP_CONNECT_TIMEOUT_MILLIS);
            return socket.getOutputStream();
        } catch (IOException exc) {
            TcpErrorLogger.log(String.format("Failed to connect to %s:%s", targetHost, targetPort), exc);
            closeSocket();
            return null;
        }
    }

    private void closeSocket() {
        super.closeStream();
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to close socket: ", e);
        } finally {
            socket = null;
            output = null;
        }
    }

}
