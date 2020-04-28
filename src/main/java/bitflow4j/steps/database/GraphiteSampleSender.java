package bitflow4j.steps.database;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.Sample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fschmidt
 */
public class GraphiteSampleSender extends AbstractProcessingStep {

    protected static final Logger logger = Logger.getLogger(GraphiteSampleSender.class.getName());

    private final String endpoint;
    private final int port;
    private final String metricNameTemplate;

    Socket conn = null;
    DataOutputStream dos;

    public GraphiteSampleSender(String metricNameTemplate, String endpoint, int port) throws IOException {
        this.port = port;
        this.endpoint = endpoint;
        this.metricNameTemplate = metricNameTemplate;
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
    }

    @Override
    public void handleSample(Sample sample) throws IOException {
        long unixTimeStamp = sample.getTimestamp().getTime() / 1000;

        for (int i = 0; i < sample.getHeader().header.length; i++) {
            output(sample.getHeader().header[i], String.valueOf(sample.getMetrics()[i]), unixTimeStamp, sample);
        }

        for (String tagKey : sample.getTags().keySet()) {
            String val = sample.getTags().get(tagKey);
            try {
                Double.parseDouble(val);
            } catch (NumberFormatException e) {
                continue;
            }
            output(tagKey, val, unixTimeStamp, sample);

        }
        output(sample);
    }

    private void output(String name, String value, long unixTimeStamp, Sample sample) {
        try {
            ensureConnection();

            String graphiteMetric = sample.resolveTagTemplate(metricNameTemplate);
            graphiteMetric = graphiteMetric.replace("[^A-Za-z0-9]", "_");
            graphiteMetric = graphiteMetric.replace(".", "_");
            graphiteMetric = graphiteMetric.replace("-", "_");

            String data = graphiteMetric + " " + value + " " + unixTimeStamp;
            logger.log(Level.INFO, "Sending to graphite: {0}", data);
            dos.writeBytes(data + "\n");
        } catch (IOException e) {
            // Do not propagate output errors, just log.
            logger.log(Level.SEVERE, "Failed to send data to Graphite", e);
        }
    }

    private void ensureConnection() throws IOException {
        if (dos == null || conn == null || conn.isClosed() || !conn.isConnected()) {
            conn = new Socket(endpoint, port);
            dos = new DataOutputStream(conn.getOutputStream());
        }
    }

    @Override
    public void cleanup() throws IOException {
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to close Grafana output connection", e);
            }
        }
        dos = null;
        conn = null;
    }

}
