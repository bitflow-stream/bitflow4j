package MetricIO;

import Marshaller.Marshaller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by anton on 4/6/16.
 */
public class TcpMetricsListener implements InputStreamProducer {

    private final Marshaller marshaller;
    private final ServerSocket tcpSocket;
    private MetricInputAggregator aggregator;

    public TcpMetricsListener(int port, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        this.tcpSocket = new ServerSocket(port);
        System.err.println("Listening on port " + port);
    }

    public void start(MetricInputAggregator aggregator) {
        this.aggregator = aggregator;
        forkAcceptConnections();
    }

    private void forkAcceptConnections() {
        Thread t = new Thread() {
            public void run() {
                TcpMetricsListener.this.acceptConnections();
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private void acceptConnections() {
        while (true) {
            Socket socket = null;
            try {
                socket = tcpSocket.accept();
                if (socket.isConnected()) {
                    String remote = acceptConnection(socket);
                    System.err.println("Accepted connection from " + remote);
                }
            } catch(Exception exc) {
                System.err.println("Error accepting connection: " + exc.getMessage());
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Error closing socket: " + e.getMessage());
                    }
                }
            }
        }
    }

    private String acceptConnection(Socket socket) throws IOException {
        String remote = socket.getRemoteSocketAddress().toString();
        MetricReader input = new MetricReader(socket.getInputStream(), marshaller);
        aggregator.addInput(remote, input);
        return remote;
    }

}
