package metrics.algorithms.rest;

import com.google.gson.reflect.TypeToken;
import metrics.algorithms.Algorithm;
import metrics.algorithms.clustering.clustering.moa.BICOClusterer;
import metrics.algorithms.clustering.clustering.moa.MOAClusteringModel;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.AbstractClusterer;
import moa.core.AutoExpandVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

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
    private static final String GRAPHS_JS_PATH = "";

    public GrahWebServer(String hostname, int port) {
        super(hostname, port);
    }

    public GrahWebServer(int port) {
        super(port);
    }

    @Override
    protected Response algorithmEndoint(IHTTPSession session, String uri, String[] splitUri) {
        //TODO short hook to get fast result, delete on merge
        Algorithm alg = this.getAlgorithm(splitUri[2]);
        BICOClusterer alg1 = (BICOClusterer) alg;
        Object model1 = alg1.getModel();
        MOAClusteringModel model = (MOAClusteringModel) model1;
        model.useMicroClusters();
        String html1 = "<script src=\"http://d3js.org/d3.v3.min.js\"></script>\n" +
                "<script src=\"https://syntagmatic.github.io/parallel-coordinates/d3.parcoords.js\"></script>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://syntagmatic.github.io/parallel-coordinates/d3.parcoords.css\">\n" +
                "<div id=\"example\" class=\"parcoords\" style=\"width:10000px;height:600px\"></div>\n" +
                "\n" +
                "<script>\n" +
                "var data = ";
        String html2 = ";\n" +
                "\n" +
                "var pc = d3.parcoords()(\"#example\")\n" +
                "  .data(data)\n" +
                "  .render()\n" +
                "  .createAxes();\n" +
                "</script>";
        String finalS = html1 + model.getClustering().getGraphJson() + html2;
        if (true) return makeHTMLResponse(finalS);
        //TODO end hook
        System.out.println("URI: " + session.getUri());
        Response response = null;
        if (uri.endsWith(".html")){
            FileInputStreamWithSize assetInputStream = null;
            try {
                assetInputStream = getAssetAsStream(uri);
            } catch (FileNotFoundException e){
                return super.algorithmEndoint(session, uri, splitUri);
            }
            response = makeHTMLResponse(assetInputStream, assetInputStream.getSize());
        }
        else if (uri.endsWith(".css")){
            FileInputStreamWithSize assetInputStream = null;
            try {
                assetInputStream = getAssetAsStream(uri);
            } catch (FileNotFoundException e){
                return super.algorithmEndoint(session, uri, splitUri);
            }
            response = makeCSSResponse(assetInputStream, assetInputStream.getSize());
        }
        else if (uri.endsWith(".js")){
            FileInputStreamWithSize assetInputStream = null;
            try {
                assetInputStream = getAssetAsStream(uri);
            } catch (FileNotFoundException e){
                return super.algorithmEndoint(session, uri, splitUri);
            }
            response = makeJSResponse(assetInputStream, assetInputStream.getSize());
        }
    else if (splitUri.length < 4) {
            return super.algorithmEndoint(session, uri, splitUri);
        } else {

            Algorithm targetAlgorithm = this.getAlgorithm(splitUri[2]);
            if (targetAlgorithm == null) {
                System.out.println("not found");
                response = makeTextResponse("Algorithm " + splitUri[2] + " not found.", Response.Status.NO_CONTENT);
            }
            switch (splitUri[3]) {
                case GRAPH_ENPDOINT: {
                    Map<String, String> requestParameters = session.getParms();
                    try {
                        AbstractClusterer castedModel = (AbstractClusterer) targetAlgorithm.getModel();
                        boolean useMicroClusters = (requestParameters.containsKey("useMicroClusters") && requestParameters.get("useMicroClusters").equalsIgnoreCase("true"))
                                ? true : false;
                        Clustering clustering = castedModel.getMicroClusteringResult();
                        AutoExpandVector<Cluster> clusteringVector = clustering.getClustering();
                        String json = this.gson.toJson(clusteringVector, new TypeToken<AutoExpandVector<Cluster>>(){}.getType());
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
//                case GRAPH_JS_ENPDOINT:
//                    try {
//                        FileInputStreamWithSize graphHtmlInputStream = getAssetAsStream(GRAPHS_JS_PATH);
//                        response = makeHTMLResponse(graphHtmlInputStream, graphHtmlInputStream.size);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                        return internalServerFault();
//                    }
//                    break;

                default: {
                    return super.algorithmEndoint(session, uri, splitUri);
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
