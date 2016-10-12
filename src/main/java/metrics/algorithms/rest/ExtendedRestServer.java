package metrics.algorithms.rest;

import metrics.algorithms.Algorithm;
import metrics.algorithms.clustering.ClusterReader;
import metrics.algorithms.clustering.clustering.moa.MOAClusteringModel;
import metrics.main.Main;
import moa.clusterers.AbstractClusterer;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by Malcolm-X on 16.09.2016.
 */
public class ExtendedRestServer extends RestServer {

    private static final Logger logger = Logger.getLogger(ExtendedRestServer.class.getName());

    public static Random random = new Random();
    private static String outputPath = "C:\\Users\\Malcolm-X\\Desktop\\";

    public ExtendedRestServer(String hostname, int port) {
        super(hostname, port);
    }

    public ExtendedRestServer(int port) {
        super(port);
    }

    protected static void startInThread(Algorithm a) {
        Runnable r;
        Thread t;
        r = new Runner(a);
        t = new Thread(r);
        t.start();
    }

    private static void startPipeAndWait(Algorithm algorithm, String outputPath) {
        //Algorithm algorithm = this.getAlgorithm("first_bico");
        //TODO apichange
        AbstractClusterer clusterer = ((MOAClusteringModel) algorithm.getModel()).getModel();
        try {
            Main.printModelPipeline(null, outputPath + random.nextInt(), new ClusterReader(clusterer)).runAndWait();
            Thread.sleep(50000000000L);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getUri().equals("/startclusterreader")) {
            Algorithm first_bico = this.getAlgorithm("first_bico");
            if (first_bico == null) logger.severe("bico should not be null");
            startInThread(first_bico);
        }
        return super.serve(session);
    }

    private static class Runner implements Runnable {
        private final Algorithm a;
        public Runner(Algorithm a) {
            this.a = a;
        }

        @Override
        public void run() {
            startPipeAndWait(a, outputPath);

        }
    }
}
