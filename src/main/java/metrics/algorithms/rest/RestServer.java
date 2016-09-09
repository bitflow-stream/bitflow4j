package metrics.algorithms.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import metrics.algorithms.Algorithm;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class will serve information on running algorithms via a rest api.
 */
public class RestServer extends NanoHTTPD {
    private final List<Algorithm> algorithms = new CopyOnWriteArrayList<>();
    Gson gson = new Gson();


    public RestServer(String hostname, int port) {
        super(hostname, port);
    }

    public RestServer(int port) {
        super(port);
    }

    private static Response makeJSONResponse(String message) {
        return makeJSONResponse(message, Response.Status.OK);
    }

    private static Response makeJSONResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, "application/json", message);
    }

    private static Response makeHTMLResponse(String message) {
        return makeHTMLResponse(message, Response.Status.OK);
    }

    private static Response makeHTMLResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, "text/html", message);
    }

    private static Response makeTextResponse(String message) {
        return makeTextResponse(message, Response.Status.OK);
    }

    private static Response makeTextResponse(String message, Response.IStatus status) {
        return newFixedLengthResponse(status, "text/plain", message);
    }

    public void addAlgorithm(Algorithm algorithm) {
        this.algorithms.add(algorithm);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;
        String uri = session.getUri();
        if (uri.startsWith("/algorithms")) {
            //TODO works different in java 7
            String[] splitUri = uri.split("/");
            switch (splitUri.length) {
                case 2:
                    System.out.println("length 2, algorithms: " + algorithms.size());
                    //IntStream.range(0,algorithms.size()).toArray(), new TypeToken<int[]>(){}.getType()
                    //response = (algorithms != null && !algorithms.isEmpty()) ? makeJSONResponse(gson.toJson(algorithms, new TypeToken<Algorithm>(){}.getType())) : makeTextResponse("No algorithm found.", Response.Status.NO_CONTENT);
                    System.out.println(gson.toJson(Integer.valueOf(algorithms.size()), new TypeToken<Integer>() {
                    }.getType()));
                    response = (algorithms != null && !algorithms.isEmpty()) ? makeJSONResponse(gson.toJson(algorithms.size())) : makeTextResponse("No algorithm found.", Response.Status.NO_CONTENT);
                    break;
                case 3:
                    System.out.println("length 3, uri: " + splitUri[2]);
                    try {
                        int algId = Integer.parseInt(splitUri[2]);
                        Algorithm algorithm = algorithms.get(algId);
                        if (algorithm == null) {
                            System.out.println("not found");
                            response = makeTextResponse("Algorithm " + algId + " not found.", Response.Status.NO_CONTENT);
                            break;
                        }
                        Object model = algorithm.getModel();
                        if (model == null || !(model instanceof Serializable)) {
                            System.out.println("model null");
                            System.out.println();
                            response = makeTextResponse("No model for algorithm " + algId + " found.", Response.Status.NO_CONTENT);
                            break;
                        }
                        System.out.println("model not null");
                        String json = gson.toJson(model, model.getClass());
                        System.out.println("json: " + json);
                        response = makeJSONResponse(json);
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
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

    @Override
    public void start() throws IOException {
        super.start(SOCKET_READ_TIMEOUT, false);
    }

}
