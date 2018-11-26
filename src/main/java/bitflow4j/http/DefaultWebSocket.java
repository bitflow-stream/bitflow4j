package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 11.01.17.
 */
public class DefaultWebSocket extends WebSocket {

    private static final Logger logger = Logger.getLogger(DefaultWebSocket.class.getName());

    public DefaultWebSocket(IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onOpen() {
        logger.info("WebSocket opened on " + getHandshakeRequest());
    }

    @Override
    protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        logger.info("WebSocket closed on " + getHandshakeRequest() + ". By remote: " + initiatedByRemote
                + ". Reason: " + reason + ". Code: " + code);
    }

    @Override
    protected void onMessage(WebSocketFrame message) {
        logger.info("Unhandled WebSocket message on " + getHandshakeRequest() + ": " + message);
    }

    @Override
    protected void onPong(WebSocketFrame pong) {
        logger.info("Unhandled WebSocket Pon on " + getHandshakeRequest() + ": " + pong);
    }

    @Override
    protected void onException(IOException exception) {
        logger.log(Level.WARNING, "Unhandled WebSocket Exception on " + getHandshakeRequest() + ": " + exception);
    }

}
