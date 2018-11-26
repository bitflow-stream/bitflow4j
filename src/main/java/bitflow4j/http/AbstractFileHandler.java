package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 16.03.17.
 */
public abstract class AbstractFileHandler extends Handler {

    private static final Logger logger = Logger.getLogger(AbstractFileHandler.class.getName());

    private static final Map<String, String> mimeTypes = new HashMap<>();

    public AbstractFileHandler() {
        super(NanoHTTPD.Method.GET);
    }

    static {
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("html", "text/html");
    }

    protected abstract InputStream openFile(RouterNanoHTTPD.UriResource uri, String path) throws IOException;

    protected abstract String getFileMimeType(RouterNanoHTTPD.UriResource uri, String path);

    public static String getMimeType(String name) {
        String result = URLConnection.guessContentTypeFromName(name);

        if (result == null || result.isEmpty()) {
            int index = name.lastIndexOf(".");
            if (index >= 0) {
                String ending = name.substring(index + 1);
                if (mimeTypes.containsKey(ending)) {
                    result = mimeTypes.get(ending);
                }
            }
        }

        if (result == null || result.isEmpty()) {
            logger.warning("Failed to guess mimetype of file: " + name);
            result = "application/octet-stream";
        }
        return result;
    }

    public String getResourcePath(RouterNanoHTTPD.UriResource uri, NanoHTTPD.IHTTPSession session) {
        String baseUri = uri.getUri();
        String realUri = RouterNanoHTTPD.normalizeUri(session.getUri());

        // Cut the baseUri part off of the requested URI
        for (int index = 0; index < Math.min(baseUri.length(), realUri.length()); index++) {
            if (baseUri.charAt(index) != realUri.charAt(index)) {
                realUri = RouterNanoHTTPD.normalizeUri(realUri.substring(index));
                break;
            }
        }

        return realUri;
    }

    @Override
    public NanoHTTPD.Response handle_uri(RouterNanoHTTPD.UriResource uri, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) throws HandlerException {
        String resource = getResourcePath(uri, session);
        InputStream stream;
        try {
            stream = openFile(uri, resource);
        } catch (IOException e) {
            throw new HandlerException(e);
        }
        if (stream == null) {
            return Server.textResponse(NanoHTTPD.Response.Status.NOT_FOUND, "Resource not found: " + resource);
        }
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, getFileMimeType(uri, resource), stream);
    }

}
