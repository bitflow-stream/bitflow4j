package metrics.algorithms.rest;

import metrics.algorithms.Algorithm;
import metrics.algorithms.clustering.clustering.moa.MOAClusteringModel;
import moa.clusterers.kmeanspm.BICO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Support for graph visualisation. Interface is available on <host>:<port>/graphs
 */
public class GraphWebServer extends RestServer {

    private static final Logger logger = Logger.getLogger(GraphWebServer.class.getName());

//    private final static String JS_PREFIX = "js";
//    private final static String CSS_PREFIX = "css";
    private final static String GRAPH_ENPDOINT = "graphs.html";
    private final static String CLUSTERING_ENDPOINT = "clustering";
    private final static String MODEL_ENDPOINT = "model";
//    private final static String GRAPH_JS_ENPDOINT = "graphs.js";
//    private final static String GRAPH_PREFIX = "graphs";
//    private final static String GRAPHS_HTML_PATH = "html/graphs.html";
//    private static final String GRAPHS_JS_PATH = "";

    public GraphWebServer(String hostname, int port) {
        super(hostname, port);
    }

    public GraphWebServer(int port) {
        super(port);
    }


    @Override
    protected Response algorithmEndoint(IHTTPSession session, String uri, String[] splitUri) {
        logger.info("function enter");
        //TODO short hook to get fast result, delete on merge
//        logger.info("AAAAAAAAA");
//        Algorithm alg = this.getAlgorithm(splitUri[2]);
//        BICOClusterer alg1 = (BICOClusterer) alg;
//        Object model1 = alg1.getModel();
//        MOAClusteringModel model = (MOAClusteringModel) model1;
//        model.useMicroClusters();
//        logger.info("CCCCCCCCCCCCCCCCCc");
//        String html1 = "<script src=\"http://d3js.org/d3.v3.min.js\"></script>\n" +
//                "<script src=\"https://syntagmatic.github.io/parallel-coordinates/d3.parcoords.js\"></script>\n" +
//                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://syntagmatic.github.io/parallel-coordinates/d3.parcoords.css\">\n" +
//                "<div id=\"example\" class=\"parcoords\" style=\"width:10000px;height:600px\"></div>\n" +
//                "\n" +
//                "<script>\n" +
//                "var data = ";
//        String html2 = ";\n" +
//                "\n" +
//                "var pc = d3.parcoords()(\"#example\")\n" +
//                "  .data(data)\n" +
//                "  .render()\n" +
//                "  .createAxes();\n" +
//                "</script>";
//        String finalS = html1 + model.getClustering().getGraphJson() + html2;
//        logger.info("BBBBBBBBBBBBB");
//        if (true) return makeHTMLResponse(finalS);
        //TODO end hook
        logger.info("URI: " + session.getUri());
        Response response = null;
        if (uri.endsWith(".html")){
            FileInputStreamWithSize assetInputStream = null;
            try {
                //TODO make more generic (e.g. by adding the algorithm as a request parameter)
                assetInputStream = getAssetAsStream("html/" + splitUri[3]);
                logger.info("got assets stream");
            } catch (FileNotFoundException e){
                //TODO
//                return super.algorithmEndoint(session, uri, splitUri);
                logger.severe("failed load 1");
                return makeTextResponse("Asset not found: " + uri, Response.Status.NOT_FOUND);
            } catch (Exception e){
                e.printStackTrace();
            }
            logger.info("process html response");
            response = makeHTMLResponse(assetInputStream, assetInputStream.getSize());
        }
        else if (uri.endsWith(".css")){
            FileInputStreamWithSize assetInputStream = null;
            try {
                assetInputStream = getAssetAsStream(uri);
            } catch (FileNotFoundException e){
//                return super.algorithmEndoint(session, uri, splitUri);
                logger.severe("failed load 2");
                return makeTextResponse("Asset not found: " + uri, Response.Status.NOT_FOUND);
            }
            logger.info("process css response");
            response = makeCSSResponse(assetInputStream, assetInputStream.getSize());
        }
        else if (uri.endsWith(".js")){
            FileInputStreamWithSize assetInputStream = null;
            try {
//                assetInputStream = getAssetAsStream(uri);
                assetInputStream = getAssetAsStream("js/graphs.js");
            } catch (FileNotFoundException e){
                logger.severe("failed load 3");
//                return super.algorithmEndoint(session, uri, splitUri);
                return makeTextResponse("Asset not found: " + uri, Response.Status.NOT_FOUND);
            }
            logger.info("process js response");
            response = makeJSResponse(assetInputStream, assetInputStream.getSize());
        }
    else if (splitUri.length < 4) {
//            return super.algorithmEndoint(session, uri, splitUri);
            logger.severe("failed load 4");
            return makeTextResponse("some error" , Response.Status.INTERNAL_ERROR);
        } else {

            Algorithm targetAlgorithm = this.getAlgorithm(splitUri[2]);
            if (targetAlgorithm == null) {
                logger.warning("not found");
                response = makeTextResponse("Algorithm " + splitUri[2] + " not found.", Response.Status.NO_CONTENT);
            }
            switch (splitUri[3]) {
                case CLUSTERING_ENDPOINT: {
                    Map<String, String> requestParameters = session.getParms();
                    try {
                        MOAClusteringModel<BICO> castedModel = (MOAClusteringModel<BICO>) targetAlgorithm.getModel();
                        String json = castedModel.getClustering().getGraphJson();
                        logger.info("got graph json: " + json);
                        response = makeJSONResponse(json);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        return makeTextResponse("Algorithm does not support graph visualisation", Response.Status.METHOD_NOT_ALLOWED);
                    } catch ( NullPointerException e){
                        e.printStackTrace();
                        return makeTextResponse("No such algorithm, or no clustering for algorithm.", Response.Status.NOT_FOUND);
                    }
                    break;
                }

                case MODEL_ENDPOINT: {
                    //done in superclass
                }

                default: {
                    logger.severe("failed load 1");
                    return makeTextResponse("somehow we reached the end of the switch case statement without finding the endpoint.");
                    //return super.algorithmEndoint(session, uri, splitUri);
                }
            }
        }
        return response;
    }

    private FileInputStreamWithSize getAssetAsStream(String path) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());
        FileInputStreamWithSize result = new FileInputStreamWithSize(file);
        return result;
    }

}
