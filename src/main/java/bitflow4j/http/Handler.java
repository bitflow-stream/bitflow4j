package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResponder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 06.01.17.
 */
public class Handler implements UriResponder {

    private static final Logger logger = Logger.getLogger(Handler.class.getName());

    protected final Set<Method> methods = new HashSet<>();

    public Handler(Method method) {
        this.methods.add(method);
    }

    public Handler(Method... methods) {
        for(Method method : methods)
            this.methods.add(method);
    }

    private Response methodNotAllowed(Method method) {
        return Server.textResponse(Status.METHOD_NOT_ALLOWED, "Method " + method + " not supported");
    }

    public Response handle(Map<String, String> urlParams, IHTTPSession session) {
        return Server.textResponse(Status.NOT_FOUND, "Page not found");
    }

    public Response handle_uri(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) throws HandlerException {
        return handle(urlParams, session);
    }

    public Response private_handle(Method method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        if (!this.methods.contains(method)) {
            return methodNotAllowed(method);
        }
        try {
            return handle_uri(uriResource, urlParams, session);
        } catch (HandlerException e) {
            return Server.textResponse(e.status, e.message);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception in HTTP handler " + uriResource, e);
            return Server.textResponse(Response.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return private_handle(Method.GET, uriResource, urlParams, session);
    }

    @Override
    public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return private_handle(Method.PUT, uriResource, urlParams, session);
    }

    @Override
    public Response post(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return private_handle(Method.POST, uriResource, urlParams, session);
    }

    @Override
    public Response delete(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return private_handle(Method.DELETE, uriResource, urlParams, session);
    }

    @Override
    public Response other(String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return private_handle(Method.valueOf(method), uriResource, urlParams, session);
    }

    public static String getParameter(IHTTPSession session, String name) throws HandlerException {
        List<String> values = session.getParameters().get(name);
        if (values == null || values.isEmpty()) {
            throw new HandlerException(Status.BAD_REQUEST, "Missing required parameter: " + name);
        }
        return values.get(0);
    }

    public static class HandlerException extends Exception {

        public final Response.IStatus status;
        public final String message;

        public HandlerException(Response.IStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public HandlerException(String message) {
            this(Response.Status.INTERNAL_ERROR, message);
        }

        public HandlerException(Exception e) {
            this(e.getMessage());
        }
    }
}
