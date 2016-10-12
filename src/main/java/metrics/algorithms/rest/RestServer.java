package metrics.algorithms.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import metrics.algorithms.Algorithm;
import moa.clusterers.kmeanspm.BICO;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * This class will serve information on running algorithms via a rest api.
 */
public class RestServer extends NanoHTTPD {

    private static final Logger logger = Logger.getLogger(RestServer.class.getName());

    public static final String ALGORITHMS_ENDPOINT = "/algorithms";
    public static final String LEGAL_CHARACTERS = "a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _";
    private static final String MIME_TEXT_HTML = "text/html";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String MIME_APPLICATION_JSON = "application/json";
    private static final String MIME_CSS = "text/css";
    private static final String MIME_JS = "application/javascript";
    private final Map<String, Algorithm> algorithms = new ConcurrentHashMap<>();
    Gson gson;

    public RestServer(String hostname, int port) {
        super(hostname, port);
        this.gson = generateGson();
    }

    protected Gson generateGson() {
        return new Gson();
    }

    public RestServer(int port) {
        super(port);
    }

    protected static Response makeJSONResponse(String message) {
        return makeJSONResponse(message, Response.Status.OK);
    }

    protected static Response makeJSONResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_APPLICATION_JSON, message);
    }

    protected static Response makeHTMLResponse(String message) {
        return makeHTMLResponse(message, Response.Status.OK);
    }

    protected static Response makeHTMLResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_TEXT_HTML, message);
    }

    protected static Response makeTextResponse(String message) {
        return makeTextResponse(message, Response.Status.OK);
    }

    protected static Response makeTextResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_TEXT_PLAIN, message);
    }

    protected static Response makeJSONResponse(InputStream in, long totalBytes) {
        return makeJSONResponse(in, totalBytes, Response.Status.OK);
    }

    protected static Response makeJSONResponse(InputStream in, long totalBytes, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_APPLICATION_JSON, in, totalBytes);
    }

    protected static Response makeHTMLResponse(InputStream in, long totalBytes) {
        return makeHTMLResponse(in, totalBytes, Response.Status.OK);
    }

    protected static Response makeHTMLResponse(InputStream in, long totalBytes, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_TEXT_HTML, in, totalBytes);
    }

    protected static Response makeTextResponse(InputStream in, long totalBytes) {
        return makeTextResponse(in, totalBytes, Response.Status.OK);
    }

    protected static Response makeTextResponse(InputStream in, long totalBytes, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_TEXT_PLAIN, in, totalBytes);
    }

    private static boolean noIllegalCharacters(String name) {
        return name.matches("^[a-zA-Z0-9_]+$");
    }

    private static String getlegalCharacters() {
        return LEGAL_CHARACTERS;
    }

    protected Response makeJSResponse(FileInputStreamWithSize assetInputStream, long size) {
        return makeJSResponse(assetInputStream, size, Response.Status.OK);
    }

    protected Response makeJSResponse(FileInputStreamWithSize assetInputStream, long size, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_JS, assetInputStream, size);
    }

    protected Response makeCSSResponse(FileInputStreamWithSize assetInputStream, long size) {
        return makeCSSResponse(assetInputStream, size, Response.Status.OK);
    }

    protected Response makeCSSResponse(FileInputStreamWithSize assetInputStream, long size, Response.IStatus status) {
        return newFixedLengthResponse(status, MIME_CSS, assetInputStream, size);
    }

    public Algorithm getAlgorithm(String name) {
        return this.algorithms != null ? algorithms.get(name) : null;
    }

    public Algorithm removeAlgorithm(String name) {
        throw new UnsupportedOperationException("not supported yet");
    }

    /**
     * This method will add an {@link Algorithm} to the RestServer. The Algorithm will be available under {@link #ALGORITHMS_ENDPOINT}/
     *
     * @param algorithm The algorithm.
     * @param name      The name for the algorithm (must be unique)
     * @return True if this algorithm has been added or false if the name is already in use.
     * @throws IllegalArgumentException if null is provided on any argument or the name contains illegal character (e.g. /)
     */
    public boolean addAlgorithm(Algorithm algorithm, String name) throws IllegalArgumentException {

        if (algorithm == null || name == null || name.isEmpty())
            throw new IllegalArgumentException("Algorithm and name must not be null or empty.");
        Algorithm result = null;
        if (noIllegalCharacters(name)) {
            result = this.algorithms.putIfAbsent(name, algorithm);
        } else
            throw new IllegalArgumentException("Name contains illegal characters. The following characters are allowed: " + getlegalCharacters());
        return result == null;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;
        String uri = session.getUri();
        if (uri.startsWith(ALGORITHMS_ENDPOINT)) {
            //TODO works different in java 7
            String[] splitUri = uri.split("/");
            switch (splitUri.length) {
                case 2:
                    response = listAlgorithmsEndpoint();
                    break;
                case 3:
                    //TODO handle other endpoints
                default:
                    logger.info("length 3 or more, uri: " + splitUri[2]);
                    response = algorithmEndoint(session, uri, splitUri);
                    logger.info("default");
                    break;
            }

        } else {
            response = makeTextResponse("unknown endoint", Response.Status.NOT_FOUND);
        }
//        switch (session.getUri()) {
//            case "/algorithms":
//                response = (algorithms != null && !algorithms.isEmpty()) ? makeJSONResponse(gson.toJson(algorithms, new TypeToken<Algorithm>(){}.getType())) : makeTextResponse("No algorithm found.", Response.Status.NO_CONTENT);
//                break;
//            default:
//
//                response = makeTextResponse("unknown endoint", Response.Status.NOT_FOUND);
//                break;
//        }
        return response;
    }

    protected Response algorithmEndoint(IHTTPSession session, String uri, String[] splitUri) {
        Response response;
        switch (session.getMethod()) {
            case GET: {
                Algorithm algorithm = algorithms.get(splitUri[2]);
                if (algorithm == null) {
                    logger.warning("not found");
                    response = makeTextResponse("Algorithm " + splitUri[2] + " not found.", Response.Status.NO_CONTENT);
                    break;
                }
                Object model = algorithm.getModel();
                if (model == null || !(model instanceof Serializable)) {
                    logger.warning("model null");
                    response = makeTextResponse("No model for algorithm " + splitUri[2] + " found.", Response.Status.NO_CONTENT);
                    break;
                }
                //TODO
                logger.info("model not null");
                String json = null;
                try {
                    json = gson.toJson(model, new TypeToken<BICO>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("json: " + json);
                response = makeJSONResponse(json);
                break;

            }

            case PUT:
            case POST:
            case DELETE:
            case HEAD:
            case OPTIONS:
            case TRACE:
            case CONNECT:
            case PATCH:
            default:
                response = makeTextResponse("Method " + session.getMethod() + " not supported on endpoint " + session.getUri(), Response.Status.METHOD_NOT_ALLOWED);
                break;
        }
        return response;
    }

    protected Response listAlgorithmsEndpoint() {
        Response response;
        String json = gson.toJson(algorithms.keySet(), new TypeToken<Set<String>>() {
        }.getType());
        response = makeJSONResponse(json);
        return response;
    }

    @Override
    public void start() throws IOException {
        super.start(50000, false);
    }

    protected Response internalServerFault() {
        return makeTextResponse("Internal Server Error", Response.Status.INTERNAL_ERROR);
    }

    protected static final class FileInputStreamWithSize extends FileInputStream {
        protected long size;

        public FileInputStreamWithSize(FileDescriptor fdObj) {
            super(fdObj);
            throw new UnsupportedOperationException();
        }

        public FileInputStreamWithSize(File file) throws FileNotFoundException {
            super(file);
            this.size = file.getTotalSpace();
//            throw new UnsupportedOperationException();
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
