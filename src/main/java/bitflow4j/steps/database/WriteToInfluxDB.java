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

    private final static int batchModeSize = 100;
    private final static int batchModeTimeout = 200;
    private static String databaseName;
    private final static int reconnectTimeout = 1000;

    private final String databaseURL;
    private final String username;
    private final String password;
    private final String prefix;

    private InfluxDB influxDB;

    protected static final Logger logger = Logger.getLogger(WriteToInfluxDB.class.getName());

    public WriteToInfluxDB(String databaseURL, String databaseName, String username, String password, String prefix) {
        this.databaseURL = databaseURL;
        this.username = username;
        this.password = password;
        this.prefix = prefix;
        this.databaseName = databaseName;

        influxDB = InfluxDBFactory.connect(databaseURL, username, password);

        checkConnectionAndReconnect();

        // Handle already generated database
        if(!influxDB.databaseExists(databaseName)){
            influxDB.createDatabase(databaseName);
            //Create the default policy which deletes data after 1000weeks, creates 1 replica and is (true) the default
            influxDB.createRetentionPolicy("defaultPolicy", databaseName, "1000w", 1, true);
        }

        // Logging & Enabling Batch-Saving Mode (not single calls for every sample to the DB)
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        influxDB.enableBatch(batchModeSize, batchModeTimeout, TimeUnit.MILLISECONDS);
        influxDB.setRetentionPolicy("defaultPolicy");
        influxDB.setDatabase(databaseName);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        checkConnectionAndReconnect();

        //Build database point
        String finalPrefix = sample.resolveTagTemplate(prefix);
        Point.Builder pointBuilder = Point.measurement(finalPrefix)
                .time(sample.getTimestamp().getTime(), TimeUnit.MILLISECONDS);
        for (int j = 0; j < sample.getMetrics().length; j++) {
            pointBuilder.addField(sample.getHeader().header[j], sample.getValue(j));
        }
        Point point = pointBuilder.build();
        //Send database point
        influxDB.write(point);

        output.writeSample(sample);
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
            } catch(Exception e){
                logger.log(Level.WARNING, "Interrupted", e);
                Thread.currentThread().interrupt();
            }
            influxDB = InfluxDBFactory.connect(databaseURL, username, password);
            response = influxDB.ping();
        }
    }

}
