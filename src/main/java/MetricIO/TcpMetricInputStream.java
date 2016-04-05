package MetricIO;

import Marshaller.Marshaller;
import Metrics.Sample;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by fschmidt, mwall on 31.03.16.
 *
 * error handling should be done above this class, is connection is lost catch exeception
 * outside and start new TcpMetricInputStream, new header will be set.
 * acceptConnections() should be called in a separate Thread.
 *
 */
public class TcpMetricInputStream implements MetricInputStream {

    private Marshaller marshaller = null;
    private ServerSocket tcpSocket = null;

    private DataInputStream dataInputStream = null;
    private String[] header;

    private final Lock lock = new ReentrantLock();
    private final Condition connected = lock.newCondition();

    public TcpMetricInputStream(int port, Marshaller marshaller) throws IOException {
        this.marshaller = marshaller;
        this.tcpSocket = new ServerSocket(port);
    }

    public void acceptConnections() throws IOException {
        while(true) {
            Socket connectionSocket = tcpSocket.accept();
            if (connectionSocket.isConnected()) {
                String remote = connectionSocket.getRemoteSocketAddress().toString();

                DataInputStream input = new DataInputStream(connectionSocket.getInputStream());
                try {
                    header = marshaller.unmarshallHeader(input);
                } catch (IOException exc) {
                    System.err.println("Failed to receive header from " + remote);
                    exc.printStackTrace(System.err);
                    connectionSocket.close();
                    continue;
                }

                lock.lock();
                try {
                    if (dataInputStream == null) {
                        System.err.println("Received header from " + remote + ": " + Arrays.toString(header));
                        dataInputStream = input;
                        connected.signalAll();
                    } else {
                        System.err.println("Rejecting connection from " + remote);
                        connectionSocket.close();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public Sample readSample() throws IOException {
        DataInputStream input;
        String[] header;
        lock.lock();
        try {
            while (this.dataInputStream == null) {
                try {
                    connected.await();
                } catch (InterruptedException exc){
                    // ignore
                }
            }
            input = this.dataInputStream;
            header = this.header;
        } finally {
            lock.unlock();
        }

        try {
            return marshaller.unmarshallSample(input, header);
        } catch (Throwable t) {
            input.close();
            lock.lock();
            try {
                this.dataInputStream = null;
                this.header = null;
            } finally {
                lock.unlock();
            }
            throw t;
        }
    }
}
