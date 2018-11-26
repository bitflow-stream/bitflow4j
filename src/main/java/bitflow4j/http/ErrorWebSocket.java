package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoWSD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by anton on 11.01.17.
 */
public class ErrorWebSocket implements WebSocketFactory {

    private final Response.IStatus status;
    private final String mime;
    private final String text;

    public ErrorWebSocket(IStatus status, String mime, String text) {
        this.status = status;
        this.mime = mime;
        this.text = text;
    }

    public ErrorWebSocket(RouterNanoHTTPD.DefaultHandler handler) {
        this(handler.getStatus(), handler.getMimeType(), handler.getText());
    }

    public ErrorWebSocket(Exception exc) {
        this(Response.Status.INTERNAL_ERROR, "text/plain", exc.toString());
    }

    @Override
    public NanoWSD.WebSocket createWebSocket(IHTTPSession session, UriRouter.Routed routed) {
        return new WebSocket(session);
    }

    public class WebSocket extends DefaultWebSocket {

        public WebSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(status, mime, text);
            getHandshakeResponse().setStatus(response.getStatus());
            getHandshakeResponse().setMimeType(response.getMimeType());
            getHandshakeResponse().setData(response.getData());
            super.onOpen();
        }

    }

}
