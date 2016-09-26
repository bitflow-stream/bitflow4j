package metrics.algorithms.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import metrics.algorithms.Algorithm;
import moa.clusterers.kmeanspm.BICO;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class will serve information on running algorithms via a rest api.
 */
public class RestServer extends NanoHTTPD {
    public static final String ALGORITHMS_ENDPOINT = "/algorithms";
    public static final String LEGAL_CHARACTERS = "a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z _";
    private static final String MIME_TEXT_HTML = "text/html";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String MIME_APPLICATION_JSON = "application/json";
    private final Map<String, Algorithm> algorithms = new ConcurrentHashMap<>();
    Gson gson = new Gson();

    public RestServer(String hostname, int port) {
        super(hostname, port);
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
                    System.out.println("length 3, uri: " + splitUri[2]);
                    response = algorithmEndoint(session, splitUri);
                default:
                    System.out.println("default");
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

    protected Response algorithmEndoint(IHTTPSession session, String[] splitUri) {
        Response response;
        switch (session.getMethod()) {
            case GET: {
                Algorithm algorithm = algorithms.get(splitUri[2]);
                if (algorithm == null) {
                    System.out.println("not found");
                    response = makeTextResponse("Algorithm " + splitUri[2] + " not found.", Response.Status.NO_CONTENT);
                    break;
                }
                Object model = algorithm.getModel();
                if (model == null || !(model instanceof Serializable)) {
                    System.out.println("model null");
                    System.out.println();
                    response = makeTextResponse("No model for algorithm " + splitUri[2] + " found.", Response.Status.NO_CONTENT);
                    break;
                }
                System.out.println("model not null");
                String json = null;
                try {
                    json = gson.toJson(model, new TypeToken<BICO>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("json: " + json);
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
        super.start(SOCKET_READ_TIMEOUT, false);
    }

    protected Response internalServerFault() {
        return makeTextResponse("Internal Server Error", Response.Status.INTERNAL_ERROR);
    }
}
