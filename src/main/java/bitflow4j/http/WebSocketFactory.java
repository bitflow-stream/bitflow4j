package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

/**
 * Created by anton on 11.01.17.
 */
public interface WebSocketFactory {

    NanoWSD.WebSocket createWebSocket(NanoHTTPD.IHTTPSession session, UriRouter.Routed routed);

}
