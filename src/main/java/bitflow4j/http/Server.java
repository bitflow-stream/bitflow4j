package bitflow4j.http;

import bitflow4j.misc.Pair;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoWSD;
import fi.iki.elonen.router.RouterNanoHTTPD.Error404UriHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 06.01.17.
 */
public class Server extends NanoWSD {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static final Map<Pair<String, Integer>, Server> runningServers = new HashMap<>();

    private final UriRouter httpRouter = new UriRouter();
    private final UriRouter websocketRouter = new UriRouter();
    private final UriResource notFoundResource = new UriResource(null, 100, Error404UriHandler.class);

    public Server(int port) {
        super(port);
    }

    public Server(String hostname, int port) {
        super(hostname, port);
    }

    public static Server on(int port) {
        return on(null, port);
    }

    public static Server on(String hostname, int port) {
        Pair<String, Integer> key = new Pair<>(hostname, port);
        if (runningServers.containsKey(key)) {
            return runningServers.get(key);
        }
        try {
            Server server = new Server(hostname, port);
            server.start();
            runningServers.put(key, server);
            return server;
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Failed to start web server (host %s, port %s)", hostname, port), e);
            return null;
        }
    }

    @Override
    public void start() throws IOException {
        start(-1); // No timeout by default
        logger.info("Serving HTTP on " + getHostname() + ":" + getListeningPort());
    }

    public void addRoute(String url, Class<? extends Handler> handler, Object... initParameter) {
        httpRouter.addRoute(url, handler, initParameter);
    }

    public void addWebSocket(String url, WebSocketFactory factory) {
        websocketRouter.addRoute(url, Object.class, factory); // Any non-null class, should not be evaluated
    }

    @Override
    protected boolean isWebsocketRequested(IHTTPSession session) {
        return websocketRouter.route(session) != null && super.isWebsocketRequested(session);
    }

    @Override
    protected Response serveHttp(IHTTPSession session) {
        UriRouter.Routed routed = httpRouter.route(session);
        if (routed == null) {
            return notFoundResource.process(Collections.emptyMap(), session);
        }
        return routed.uriResource.process(routed.urlParameters, session);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        UriRouter.Routed routed = websocketRouter.route(handshake);
        if (routed == null) {
            return new ErrorWebSocket(new Error404UriHandler()).createWebSocket(handshake, null);
        }
        WebSocketFactory factory = routed.uriResource.initParameter(WebSocketFactory.class);
        return factory.createWebSocket(handshake, routed);
    }

    public static Response textResponse(IStatus status, String text) {
        return NanoHTTPD.newFixedLengthResponse(status, "text/plain", text);
    }

    public static Response jsonResponse(IStatus status, String jsonString) {
        return NanoHTTPD.newFixedLengthResponse(status, "text/json", jsonString);
    }

    public static Response errorResponse(String message, Exception exception) {
        message = message + ": " + exception.getMessage();
        logger.log(Level.SEVERE, message, exception);
        return textResponse(Response.Status.INTERNAL_ERROR, message);
    }

}
