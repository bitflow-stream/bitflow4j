package metrics.algorithms.rest;

import metrics.algorithms.Algorithm;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Support for graph visualisation. Interface is available on <host>:<port>/graphs
 */
public class GrahWebServer extends RestServer {

    private final static String JS_PREFIX = "js";
    private final static String CSS_PREFIX = "css";
    private final static String GRAPH_ENPDOINT = "graphs.html";
    private final static String GRAPH_JS_ENPDOINT = "graphs.js";
    private final static String GRAPH_PREFIX = "graphs";
    private final static String GRAPHS_HTML_PATH = "html/graphs.html";

    public GrahWebServer(String hostname, int port) {
        super(hostname, port);
    }

    public GrahWebServer(int port) {
        super(port);
    }

    @Override
    protected Response algorithmEndoint(IHTTPSession session, String[] splitUri) {
        if (splitUri.length < 4) {
            return super.algorithmEndoint(session, splitUri);
        } else {
            Response response = null;
            Algorithm targetAlgorithm = this.getAlgorithm(splitUri[2]);
            if (targetAlgorithm == null) {
                System.out.println("not found");
                response = makeTextResponse("Algorithm " + splitUri[2] + " not found.", Response.Status.NO_CONTENT);
            }
            switch (splitUri[3]) {
                case GRAPH_ENPDOINT: {
                    try {
                        FileInputStreamWithSize graphHtmlInputStream = getAssetAsStream(GRAPHS_HTML_PATH);
                        response = makeHTMLResponse(graphHtmlInputStream, graphHtmlInputStream.size);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return internalServerFault();
                    }
                    break;
                }
                case GRAPH_JS_ENPDOINT:
                    try {
                        FileInputStreamWithSize graphHtmlInputStream = getAssetAsStream(GRAPHS_JS_PATH);
                        response = makeHTMLResponse(graphHtmlInputStream, graphHtmlInputStream.size);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return internalServerFault();
                    }
                    break;

            }
            return response;
        }
    }

    private FileInputStreamWithSize getAssetAsStream(String path) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());
        FileInputStreamWithSize result = new FileInputStreamWithSize(file);
        return result;
    }

    private static final class FileInputStreamWithSize extends FileInputStream {
        private long size;

        public FileInputStreamWithSize(FileDescriptor fdObj) {
            super(fdObj);
            throw new UnsupportedOperationException();
        }

        public FileInputStreamWithSize(File file) throws FileNotFoundException {
            super(file);
            this.size = file.getTotalSpace();
            throw new UnsupportedOperationException();
        }

        public FileInputStreamWithSize(String name) throws FileNotFoundException {
            super(name);
            throw new UnsupportedOperationException();
        }

        public long getSize() {
            return this.size;
        }

    }
}
