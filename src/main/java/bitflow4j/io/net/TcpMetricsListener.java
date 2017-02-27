package bitflow4j.io.net;

import bitflow4j.io.MetricReader;
import bitflow4j.io.ThreadedSampleSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 4/6/16.
 */
public class TcpMetricsListener extends ThreadedSampleSource {

    private static final Logger logger = Logger.getLogger(TcpMetricsListener.class.getName());

    private int numConnections = 0;
    private final int maxNumConnections;
    private final int port;
    private final Marshaller marshaller;

    private ConnectionAcceptor connectionAcceptor;

    public TcpMetricsListener(int port, Marshaller marshaller) throws IOException {
        this(port, marshaller, -1);
    }

    public TcpMetricsListener(int port, Marshaller marshaller, int numConnections) throws IOException {
        this.maxNumConnections = numConnections;
        this.marshaller = marshaller;
        this.port = port;
    }

    public void start(TaskPool pool) throws IOException {
        logger.info("Listening on port " + port);
        ServerSocket tcpSocket = new ServerSocket(port);
        connectionAcceptor = new ConnectionAcceptor(tcpSocket);
        pool.start(connectionAcceptor);
    }

    @Override
    public void shutDown() {
        if (connectionAcceptor != null) {
            connectionAcceptor.stop();
        }
        super.shutDown();
    }

    @Override
    protected void readerException() {
        // Avoid shutting down because of Exception in connections.
        // This SampleSource runs forever (until user shuts down the program).
    }

    private class ConnectionAcceptor implements ParallelTask {
        private final ServerSocket socket;
        private TaskPool pool;

        private ConnectionAcceptor(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public String toString() {
            return "TCP listener on " + socket;
        }

        @Override
        public void start(TaskPool pool) throws IOException {
            this.pool = pool;
        }

        @Override
        public void run() throws IOException {
            acceptConnections(pool, socket);
        }

        public void stop() {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to close TCP server socket", e);
            }
        }
    }

    private void acceptConnections(TaskPool pool, ServerSocket tcpSocket) {
        while (true) {
            Socket socket = null;
            try {
                if (tcpSocket.isClosed())
                    break;
                socket = tcpSocket.accept();
                if (socket.isConnected()) {
                    String remote = acceptConnection(pool, socket);
                    logger.info("Accepted connection from " + remote);
                    if (checkNumConnections()) break;
                }
            } catch (Exception exc) {
                if (tcpSocket.isClosed())
                    break;
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

    private String acceptConnection(TaskPool pool, Socket socket) throws IOException {
        String remote = socket.getRemoteSocketAddress().toString(); // TODO try reverse DNS? More descriptive name?
        MetricReader.NamedInputStream namedStream =
                new MetricReader.NamedInputStream(socket.getInputStream(), remote);
        MetricReader input = new MetricReader(pool, marshaller) {

            boolean started = false;

            @Override
            public String toString() {
                return "Receiving samples from " + remote;
            }

            @Override
            protected synchronized NamedInputStream nextInput() throws IOException {
                if (started)
                    return null;
                started = true;
                return namedStream;
            }
        };
        readSamples(pool, input, true);
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
