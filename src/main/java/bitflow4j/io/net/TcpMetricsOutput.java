package bitflow4j.io.net;

import bitflow4j.Marshaller;
import bitflow4j.Sample;
import bitflow4j.io.AbstractMetricPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by anton on 6/17/16.
 */
public class TcpMetricsOutput extends AbstractMetricPrinter {

    private static final Logger logger = Logger.getLogger(TcpMetricsOutput.class.getName());

    private final String targetHost;
    private final int targetPort;
    private Socket socket = null;

    public TcpMetricsOutput(Marshaller marshaller, String targetHost, int targetPort) {
        super(marshaller);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public synchronized void writeSample(Sample sample) throws IOException {
        try {
            super.writeSample(sample);
        } catch(IOException exc) {
            logger.severe("Failed to send sample to " + targetHost + ":" + targetPort + ": " + exc);
            closeSocket();
        }
    }

    protected OutputStream nextOutputStream() throws IOException {
        closeSocket();
        socket = new Socket(targetHost, targetPort);
        return socket.getOutputStream();
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
