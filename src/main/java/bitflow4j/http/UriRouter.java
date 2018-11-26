package bitflow4j.http;

import fi.iki.elonen.router.RouterNanoHTTPD.DefaultRoutePrioritizer;
import fi.iki.elonen.router.RouterNanoHTTPD.IRoutePrioritizer;
import fi.iki.elonen.router.RouterNanoHTTPD.NotImplementedHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

import java.util.Map;

import static fi.iki.elonen.router.RouterNanoHTTPD.IHTTPSession;
import static fi.iki.elonen.router.RouterNanoHTTPD.normalizeUri;

/**
 * Created by anton on 11.01.17.
 */
public class UriRouter {

    private final IRoutePrioritizer routePrioritizer;

    public UriRouter() {
        routePrioritizer = new DefaultRoutePrioritizer();
        routePrioritizer.setNotImplemented(NotImplementedHandler.class);
    }

    public static class Routed {
        public final Map<String, String> urlParameters;
        public final UriResource uriResource;

        public Routed(Map<String, String> urlParameters, UriResource uriResource) {
            this.urlParameters = urlParameters;
            this.uriResource = uriResource;
        }
    }

    public Routed route(IHTTPSession session) {
        String work = normalizeUri(session.getUri());
        for (UriResource resource : routePrioritizer.getPrioritizedRoutes()) {
            Map<String, String> params = resource.match(work);
            if (params != null) {
                return new Routed(params, resource);
            }
        }
        return null;
    }

    public void addRoute(String url, Class<?> handler, Object... initParameter) {
        routePrioritizer.addRoute(url, 100, handler, initParameter);
    }

    public void addRoute(String url, int priority, Class<?> handler, Object... initParameter) {
        routePrioritizer.addRoute(url, priority, handler, initParameter);
    }

    public void removeRoute(String url) {
        routePrioritizer.removeRoute(url);
    }

}
