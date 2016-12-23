package bitflow4j.io.net;

import bitflow4j.Marshaller;
import bitflow4j.io.ActiveInputStream;
import bitflow4j.io.MetricReader;
import bitflow4j.io.aggregate.MetricInputAggregator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by anton on 4/6/16.
 */
public class TcpMetricsListener extends ActiveInputStream {

    private static final Logger logger = Logger.getLogger(TcpMetricsListener.class.getName());

    private int numConnections = 0;
    private final int maxNumConnections;
    private final Marshaller marshaller;
    private final ServerSocket tcpSocket;
    private MetricInputAggregator aggregator;

    public TcpMetricsListener(int port, Marshaller marshaller) throws IOException {
        this(port, marshaller, 1);
    }

    public TcpMetricsListener(int port, Marshaller marshaller, int numConnections) throws IOException {
        this.maxNumConnections = numConnections;
        this.marshaller = marshaller;
        this.tcpSocket = new ServerSocket(port);
        logger.info("Listening on port " + port);
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
                    logger.info("Accepted connection from " + remote);
                    if (checkNumConnections()) break;
                }
            } catch (Exception exc) {
                logger.severe("Error accepting connection: " + exc.getMessage());
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        logger.warning("Error closing socket: " + e.getMessage());
                    }
                }
            }
        }
    }

    private String acceptConnection(Socket socket) throws IOException {
        String remote = socket.getRemoteSocketAddress().toString(); // TODO try reverse DNS? More descriptive name?
        MetricReader input = new MetricReader(socket.getInputStream(), remote, marshaller);
        new ReaderThread(remote, input).start();
        return remote;
    }

    private boolean checkNumConnections() {
        numConnections++;
        if (maxNumConnections > 0 && numConnections >= maxNumConnections) {
            // TODO cannot close the socket here, because connections might still be open.
            // Just ignoring new incoming connections.
            logger.warning("Accepted " + numConnections + " connection(s). Ignoring further connections.");
            return true;
        }
        return false;
    }

}
