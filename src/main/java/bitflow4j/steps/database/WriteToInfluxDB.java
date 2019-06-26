package bitflow4j.steps.database;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Description("Writes all metrics of samples to the database with the given prefix.")
public class WriteToInfluxDB extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(WriteToInfluxDB.class.getName());

    private final static int batchModeSize = 100;
    private final static int batchModeTimeout = 200;
    private final static int reconnectTimeout = 1000;

    private final String databaseName;
    private final String databaseURL;
    private final String username;
    private final String password;
    private final String prefix;
    private final int metricsPerRequest;

    private InfluxDB influxDB;
    private boolean defaultDatabasedChecked;

    public WriteToInfluxDB(String databaseURL, String databaseName, String username, String password, String prefix) {
        // By default, do not limit the number of metrics in one request.
        this(databaseURL, databaseName, username, password, prefix, Integer.MAX_VALUE);
    }

    public WriteToInfluxDB(String databaseURL, String databaseName, String username, String password, String prefix, int metricsPerRequest) {
        this.databaseURL = databaseURL;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.prefix = prefix;
        this.metricsPerRequest = metricsPerRequest;

        influxDB = InfluxDBFactory.connect(databaseURL, username, password);

        // Logging & Enabling Batch-Saving Mode (not single calls for every sample to the DB)
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        influxDB.enableBatch(batchModeSize, batchModeTimeout, TimeUnit.MILLISECONDS);
        influxDB.setRetentionPolicy("defaultPolicy");
        influxDB.setDatabase(databaseName);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        try {
            checkConnectionAndReconnect();
            ensureDefaultDatabase();
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Failed to connect to InfluxDB at %s: %s", databaseURL, buildExceptionMessage(e)));
            return;
        }

        // Build and send database points.
        // Split the sample into parts to avoid parser errors due to  overly long request.
        for (int i = 0; i < sample.getMetrics().length; i += metricsPerRequest) {
            String finalPrefix = sample.resolveTagTemplate(prefix);

            Point.Builder pointBuilder = Point.measurement(finalPrefix)
                    .time(sample.getTimestamp().getTime(), TimeUnit.MILLISECONDS);
            for (int j = i; j < sample.getMetrics().length && j < i + metricsPerRequest; j++) {
                double val = sample.getValue(j);
                if (isValidValue(val)) {
                    // Only include valid values, because storing the point fails otherwise.
                    pointBuilder.addField(sample.getHeader().header[j], val);
                }
            }

            // Send database point
            Point point = pointBuilder.build();
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(String.format("Sending point to InfluxDB at %s, line protocol length: %s", databaseURL, point.lineProtocol().length()));
            }
            influxDB.write(point);
        }

        output.writeSample(sample);
    }

    private static String buildExceptionMessage(Throwable e) {
        StringBuilder b = new StringBuilder();
        while (e != null) {
            b.append("[");
            b.append(e.getClass().getName());
            b.append("] ");
            b.append(e.getMessage());
            e = e.getCause();
            if (e != null)
                b.append(", caused by: ");
        }
        return b.toString();
    }

    private static boolean isValidValue(double val) {
        return Double.isFinite(val);
    }

    @Override
    protected void doClose() throws IOException {
        influxDB.close();
        super.doClose();
    }

    private void checkConnectionAndReconnect() {
        Pong response = influxDB.ping();
        while (response.getVersion().equalsIgnoreCase("unknown")) {
            logger.log(Level.SEVERE, String.format("Could not connect to InfluxDB with address '%s'. Retry in %s milliseconds", databaseURL, reconnectTimeout));
            try {
                Thread.sleep(reconnectTimeout);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Interrupted", e);
                Thread.currentThread().interrupt();
            }
            influxDB = InfluxDBFactory.connect(databaseURL, username, password);
            response = influxDB.ping();
        }
    }

    private void ensureDefaultDatabase() {
        if (defaultDatabasedChecked) {
            return;
        }

        // Handle already generated database
        if (!influxDB.databaseExists(databaseName)) {
            influxDB.createDatabase(databaseName);
            // Create the default policy which deletes data after 1000weeks, creates 1 replica and is (true) the default
            influxDB.createRetentionPolicy("defaultPolicy", databaseName, "1000w", 1, true);
        }
        defaultDatabasedChecked = true;
    }

}
