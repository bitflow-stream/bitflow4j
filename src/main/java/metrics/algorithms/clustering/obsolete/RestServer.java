package metrics.algorithms.clustering.obsolete;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.Algorithm;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by malcolmx on 07.09.16.
 */
public class RestServer extends NanoHTTPD {
    private List<AbstractAlgorithm> algorithms = new CopyOnWriteArrayList<>();
    //    private Map<Integer,Algorithm> algorithms = new ConcurrentHashMap<>();
    private Gson gson = new Gson();

    public RestServer(int port) throws IOException {
        super(port);
//        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public RestServer(String hostname, int port) throws IOException {
        super(hostname, port);
//        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public synchronized void addAlgorithm(AbstractAlgorithm algorithm) throws NullPointerException {
        this.algorithms.add(algorithm);
//        this.algorithms.put(algorithm.getId(),algorithm);
//        this.
    }

    @Override
    public Response serve(IHTTPSession session) {
        System.err.println("serve");
        String response = "";
        System.out.println(session.getUri());
//        String path = session.getUri().
        response = parseUrlPath(session);
//        return super.serve(session);
        //TODO refactor to return response object directly in call-chain
        System.err.println("returning: " + response);
        return newFixedLengthResponse(Response.Status.OK, "application/json", response);
    }

    /**
     * Parse the url path and trigger correct action.
     *
     * @param session the session, forwarded from the serve method
     * @return the response corresponding the path and the chosen parameters
     */
    private String parseUrlPath(IHTTPSession session) {
        String[] splitPath = session.getUri().split("/");
        System.out.println("split uri: " + Arrays.deepToString(splitPath));
        String result = null;
        if (splitPath.length <= 1)
            return "<html><body><h1>THIS IS THE RESTCLIENT FOR THE ALGORITHM FRAMEWORK</h1></body></html>";
        switch (splitPath[1]) {
            case ("algorithms"):
                result = requestAlgorithms(session, splitPath);
                break;
            case ("other"):
                //do something else
                break;
//            case(""):
//                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Handler for requests to /algorithms*
     *
     * @param session   the forwarded session
     * @param splitPath the split uri
     * @return the String to send to the client or null if endpoint is not supported
     */
    private String requestAlgorithms(IHTTPSession session, String[] splitPath) {
        if (this.algorithms == null || this.algorithms.isEmpty()) {
            System.err.println("Algorithms empty");
            return null;
        }
        String result = null;
        if (splitPath.length == 2) {
            Method method = session.getMethod();
            switch (method) {
                case GET:
                    System.err.println("case get");
                    result = buildAlgorithmJSON();
                    System.err.println("Result: " + result);
                    break;
                case PUT:
                    //TODO not supported
                    break;
                case POST:
                    //TODO not supported
                    break;
                case DELETE:
                    //TODO not supported
                    break;
                case HEAD:
                    //TODO not supported
                    break;
                case OPTIONS:
                    //TODO not supported
                    break;
                case TRACE:
                    //TODO not supported
                    break;
                case CONNECT:
                    //TODO not supported
                    break;
                case PATCH:
                    //TODO not supported
                    break;
                default:
                    System.err.println("case default");
                    break;
            }
        } else {
            System.err.println("else");
            result = requestAlgorithm(session, splitPath);
//            switch (splitPath[2]){
//                case ()
//            }
        }
        System.err.println("returnung result: " + result);
        return result;
    }


    private String requestAlgorithm(IHTTPSession session, String[] splitPath) {
        String result = null;
        Algorithm requestedAlgorithm = null;
        Object modelOfReqAlg = null;
        int idOfReqAlg = -1;
        try {
            idOfReqAlg = Integer.parseInt(splitPath[2]);
            requestedAlgorithm = this.algorithms.get(this.algorithms.indexOf(idOfReqAlg));
//            modelOfReqAlg = requestedAlgorithm.getModel();
        } catch (NumberFormatException e) {
            return null;
        }
//        catch (Exception e) {
//            TODO add default error message
//            return null;
//        }

        if (splitPath.length == 3) {
            //TODO add extra endpoint for the model
            switch (session.getMethod()) {
                case GET:
                    //TODO return representation of an algorithm
                    result = gson.toJson(modelOfReqAlg);
                    break;
                case PUT:
                    //TODO not supported
                    break;
                case POST:
                    //TODO not supported
                    break;
                case DELETE:
                    //TODO not supported
                    break;
                case HEAD:
                    //TODO not supported
                    break;
                case OPTIONS:
                    //TODO not supported
                    break;
                case TRACE:
                    //TODO not supported
                    break;
                case CONNECT:
                    //TODO not supported
                    break;
                case PATCH:
                    //TODO not supported
                    break;
                default:
                    break;
            }

        }else {
            switch (splitPath[3]){
                case("model") : result = requestModel(session, splitPath, requestedAlgorithm);
            }
        }
        return result;
    }

    private String requestModel(IHTTPSession session, String[] splitPath, Algorithm requestedAlgorithm) {
        String result = null;
        Object modelOfReqAlg = null;
    //TODO null checking
        try {
            modelOfReqAlg = requestedAlgorithm.getModel();
        } catch (Exception e) {
            return null;
        }
        result = gson.toJson(modelOfReqAlg, modelOfReqAlg.getClass());
        return result;
    }

    private String buildAlgorithmJSON() {
        String result = null;
        int[] ids = new int[algorithms.size()];
        for (int i = 0; i < algorithms.size(); i++){
            ids[i] = algorithms.get(i).getId();
        }
        TypeToken<int[]> token;
        token = new TypeToken<int[]>(){};
        result = gson.toJson(ids, token.getType());
        return result;
//        return gson.toJson(algorithms);
    }

    @Override
    public void start() throws IOException {
        System.err.println("starting rest server");
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.err.println("finished starting rest server");
    }
}
