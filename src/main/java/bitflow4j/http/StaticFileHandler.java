package bitflow4j.http;

import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.logging.Logger;

/**
 * Created by anton on 16.03.17.
 */
public class StaticFileHandler extends StaticDirHandler {

    private static final Logger logger = Logger.getLogger(StaticFileHandler.class.getName());
    
    @Override
    protected String getFileName(RouterNanoHTTPD.UriResource uri, String path) {
        // The path is ignored, always serve the same file
        return uri.initParameter(String.class);
    }

}