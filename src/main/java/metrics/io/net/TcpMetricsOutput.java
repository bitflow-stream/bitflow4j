package metrics.io.net;

import metrics.Marshaller;
import metrics.Sample;
import metrics.io.AbstractMetricPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by anton on 6/17/16.
 */
public class TcpMetricsOutput extends AbstractMetricPrinter {

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
            System.err.println("Failed to send sample to " + targetHost + ":" + targetPort + ": " + exc);
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
            System.err.println("Failed to close socket: " + e);
        } finally {
            socket = null;
            output = null;
        }
    }

}
