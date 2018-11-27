package bitflow4j.steps.output;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.http.DefaultWebSocket;
import bitflow4j.http.UriRouter;
import bitflow4j.http.WebSocketFactory;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.TaskPool;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 11.01.17.
 */
public class WebSocketSender extends AbstractPipelineStep implements WebSocketFactory {

    private static final Logger logger = Logger.getLogger(WebSocketSender.class.getName());

    private final SynchronizedSampleWindow window;
    private final Set<WebSocket> openSockets = new HashSet<>();
    private final Gson gson = new Gson();

    public WebSocketSender(int windowSize) throws IOException {
        window = new SynchronizedSampleWindow(windowSize);
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        pool.start(new LoopSampleSender());
        super.start(pool);
    }

    public void writeSample(Sample sample) throws IOException {
        window.push(sample);
        super.writeSample(sample);
    }

    @Override
    public NanoWSD.WebSocket createWebSocket(NanoHTTPD.IHTTPSession session, UriRouter.Routed routed) {
        return new WebSocket(session);
    }

    private class LoopSampleSender extends StoppableLoopTask {

        @Override
        public String toString() {
            return "WebSocketSender (window " + window.maxSize + ")";
        }

        @Override
        public synchronized void stop() {
            super.stop();
            synchronized (window) {
                window.notifyAll();
            }
            super.waitForExit();
        }

        @Override
        public boolean executeIteration() throws IOException {
            boolean sent = false;
            for (WebSocket socket : openSockets) {
                if (!socket.isOpen()) {
                    openSockets.remove(socket);
                    return true;
                }
                Link link = socket.getLink();
                if (link != null) {
                    try {
                        sendSample(socket, link.sample);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Failed to send sample on web socket", e);
                        try {
                            socket.close(NanoWSD.WebSocketFrame.CloseCode.AbnormalClosure,
                                    "Failed to send sample on web socket: " + e, false);
                        } catch (IOException e1) {
                            logger.log(Level.SEVERE, "Failed to close web socket", e1);
                        }
                        openSockets.remove(socket);
                    }
                    sent = true;
                }
            }
            if (!sent) {
                // No sample was sent, wait for an incoming sample.
                try {
                    synchronized (window) {
                        if (!isStopped())
                            window.wait();
                    }
                } catch (InterruptedException ignored) {
                }
            }
            return true;
        }

    }

    private void sendSample(WebSocket socket, Sample sample) throws IOException {
        socket.send(marshallSample(sample));
    }

    private String marshallSample(Sample sample) {
        List<Map<String, Object>> jsonData = new ArrayList<>();
        for (int i = 0; i < sample.getHeader().header.length; i++) {
            String field = sample.getHeader().header[i];
            double value = sample.getMetrics()[i];
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", field);
            entry.put("val", value);
            jsonData.add(entry);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tags", sample.getTags());
        result.put("timestamp", sample.getTimestamp().getTime());
        result.put("data", jsonData);
        return gson.toJson(result);
    }

    private class WebSocket extends DefaultWebSocket {

        private Link link = null;

        public WebSocket(NanoHTTPD.IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        Link getLink() {
            Link result;
            if (link == null) {
                result = window.tryHead();
                logger.info("Sending " + window.size() + " buffered samples to " + getHandshakeRequest().getRemoteHostName());
            } else {
                result = link.tryNext();
            }
            if (result != null)
                link = result;
            return result;
        }

        @Override
        protected void onOpen() {
            openSockets.add(this);
            // Immediately send data from the window
            synchronized (window) {
                window.notifyAll();
            }
            super.onOpen();
        }

        @Override
        protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            openSockets.remove(this);
            super.onClose(code, reason, initiatedByRemote);
        }

    }

    private static class SynchronizedSampleWindow {

        public final int maxSize;
        private int size = 0;
        private Link first = null;
        private Link last = null;

        public SynchronizedSampleWindow(int size) {
            this.maxSize = size;
        }

        public void push(Sample sample) {
            synchronized (this) {
                if (size == 0) {
                    first = last = new Link(sample);
                    size++;
                } else {
                    Link next = new Link(sample);
                    last.setNext(next);
                    last = next;
                    if (size >= maxSize) {
                        first = first.next;
                    } else {
                        size++;
                    }
                }
                notifyAll();
            }
        }

        public Link tryHead() {
            return first;
        }

        public int size() {
            return size;
        }

        public synchronized Link head() {
            while (first == null) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
            return first;
        }
    }

    private static class Link {

        public final Sample sample;
        private Link next = null;

        Link(Sample sample) {
            this.sample = sample;
        }

        public Link tryNext() {
            return next;
        }

        public Link next() {
            if (next != null) {
                return next;
            }
            synchronized (this) {
                while (next == null) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                return next;
            }
        }

        synchronized void setNext(Link next) {
            this.next = next;
            notifyAll();
        }

    }


}
