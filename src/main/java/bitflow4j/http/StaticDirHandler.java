package bitflow4j.http;

import fi.iki.elonen.router.RouterNanoHTTPD;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by anton on 16.03.17.
 */
public class StaticDirHandler extends AbstractFileHandler {

    private static final Logger logger = Logger.getLogger(StaticDirHandler.class.getName());

    protected String getFileName(RouterNanoHTTPD.UriResource uri, String path) {
        String webRoot = uri.initParameter(String.class);
        if (!webRoot.endsWith("/") && !path.isEmpty()) webRoot += "/";
        return webRoot + path;
    }

    @Override
    protected InputStream openFile(RouterNanoHTTPD.UriResource uri, String path) throws IOException {
        FileInputStream fis = new FileInputStream(getFileName(uri, path));
        return new BufferedInputStream(fis);
    }

    @Override
    protected String getFileMimeType(RouterNanoHTTPD.UriResource uri, String path) {
        return getMimeType(getFileName(uri, path));
    }

}
