package bitflow4j.io.net;

import bitflow4j.io.AbstractSampleWriter;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.sample.Sample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 6/17/16.
 */
public class TcpSink extends AbstractSampleWriter {

    private static final Logger logger = Logger.getLogger(TcpSink.class.getName());

    public static final int TCP_CONNECT_TIMEOUT_MILLIS = 3000;

    private final String targetHost;
    private final int targetPort;
    private Socket socket = null;

    public Level connectionErrorLevel = Level.WARNING;

    public TcpSink(Marshaller marshaller, String targetHost, int targetPort) {
        super(marshaller);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public TcpSink level(Level level) {
        this.connectionErrorLevel = level;
        return this;
    }

    public synchronized void writeSample(Sample sample) throws IOException {
        try {
            super.writeSample(sample);
        } catch (IOException exc) {
            logger.log(connectionErrorLevel, "Failed to send sample to " + targetHost + ":" + targetPort + ": " + exc);
            closeSocket();
        }
    }

    protected OutputStream nextOutputStream() throws IOException {
        try {
            closeSocket();
            socket = new Socket();
            socket.connect(new InetSocketAddress(targetHost, targetPort), TCP_CONNECT_TIMEOUT_MILLIS);
            return socket.getOutputStream();
        } catch (IOException exc) {
            logger.log(connectionErrorLevel, "Failed to connect to " + targetHost + ":" + targetPort + ": ", exc);
            closeSocket();
            return null;
        }
    }

    private void closeSocket() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.warning("Failed to close socket: " + e);
        } finally {
            socket = null;
            output = null;
        }
    }

}
