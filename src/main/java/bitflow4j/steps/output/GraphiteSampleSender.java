package bitflow4j.steps.output;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

/**
 * @author fschmidt
 */
public class GraphiteSampleSender extends AbstractPipelineStep {

    private final String endpoint;
    private final int port;
    private final GraphiteMetricName metricNameConverter;

    Socket conn = null;
    DataOutputStream dos;

    public interface GraphiteMetricName {
        String metricName(String metricName, Sample sample);
    }

    public GraphiteSampleSender(GraphiteMetricName metricNameConverter, String endpoint, int port) throws IOException {
        this.port = port;
        this.endpoint = endpoint;
        this.metricNameConverter = metricNameConverter;
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");
    }

    public GraphiteSampleSender(String endpoint, int port, String prefix) throws IOException {
        this(new BicoMetricNames(prefix), endpoint, port);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        long unixTimeStamp = sample.getTimestamp().getTime() / 1000;

        for (int i = 0; i < sample.getHeader().header.length; i++) {
            output(sample.getHeader().header[i], String.valueOf(sample.getMetrics()[i]), unixTimeStamp, sample);
        }

        for (String tagKey: sample.getTags().keySet()) {
            String val = sample.getTags().get(tagKey);
            try {
                Double.parseDouble(val);
            } catch (NumberFormatException e) {
                continue;
            }
            output(tagKey, val, unixTimeStamp, sample);

        }
        super.writeSample(sample);
    }

    private void output(String name, String value, long unixTimeStamp, Sample sample) {
        try {
            ensureConnection();

            String graphiteMetric = metricNameConverter.metricName(name, sample);
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
    protected void doClose() throws IOException {
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to close Grafana output connection", e);
            }
        }
        dos = null;
        conn = null;
        super.doClose();
    }

    public static class BicoMetricNames implements GraphiteMetricName {

        private final String prefix;

        public BicoMetricNames(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String metricName(String headerName, Sample sample) {
            headerName = sample.getTag("host") + "." + headerName;
            if (!prefix.isEmpty()) {
                headerName = prefix + "." + headerName;
            }
            return headerName;
        }
    }

}
