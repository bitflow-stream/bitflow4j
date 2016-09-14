package metrics.algorithms.clustering.obsolete;

import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import metrics.algorithms.Algorithm;
import metrics.algorithms.classification.Model;

import java.io.IOException;

/**
 * Created by malcolmx on 07.09.16.
 */
public class AlgorithmRestServer extends NanoHTTPD {
    private final Algorithm algorithm;
    private Gson gson = new Gson();

    public AlgorithmRestServer(Algorithm algorithm, int port) {
        super(port);
        if (algorithm == null) throw new NullPointerException();
        this.algorithm = algorithm;
    }

    public AlgorithmRestServer(Algorithm algorithm, String hostname, int port) {
        super(hostname, port);
        if (algorithm == null) throw new NullPointerException();
        this.algorithm = algorithm;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Object model = algorithm.getModel();
        } catch (Exception e) {
            System.err.println("failed to server model for algorithm: " + this.algorithm);
        }
//        String responseString = gson.toJson(algorithm);
//            return newFixedLengthResponse(Response.Status.OK, "application/json", responseString);
            return newFixedLengthResponse(Response.Status.OK, "application/json", "HAAAAAAAAAAAAAALLOOOOOOOOO");

    }

    @Override
    public void stop() {
        super.stop();
    }
}
