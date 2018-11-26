package bitflow4j.http;

import fi.iki.elonen.router.RouterNanoHTTPD;

import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Based on RouterNanoHTTPD.StaticPageHandler, but serves files from the resources available
 * through the class loader.
 * <p>
 * Created by anton on 06.01.17.
 */
public class StaticResourceHandler extends AbstractFileHandler {

    private static final Logger logger = Logger.getLogger(StaticResourceHandler.class.getName());

    private String getFileName(RouterNanoHTTPD.UriResource uri, String path) {
        String resourceRoot = uri.initParameter(String.class);
        if (!resourceRoot.endsWith("/") && !path.isEmpty()) resourceRoot += "/";
        return resourceRoot + path;
    }

    @Override
    protected InputStream openFile(RouterNanoHTTPD.UriResource uri, String path) {
        String resourceRoot = uri.initParameter(String.class);
        if (!resourceRoot.endsWith("/") && !path.isEmpty()) resourceRoot += "/";
        return getClass().getClassLoader().getResourceAsStream(resourceRoot + path);
    }

    @Override
    protected String getFileMimeType(RouterNanoHTTPD.UriResource uri, String path) {
        return getMimeType(getFileName(uri, path));
    }

}
