package bitflow4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by anton on 4/14/16.
 * <p>
 * Static configuration loaded from a resource file.
 */
public class LoggingConfig {

    private static final Logger logger = Logger.getLogger(LoggingConfig.class.getName());

    private static final String logging_properties = "logging.properties";
    private static final String logging_properties_short = "logging-short.properties";

    public static void setDefaultLogLevel(Level level) {
        Logger root = Logger.getLogger("");
        root.setLevel(level);
        for (Handler h : root.getHandlers()) {
            h.setLevel(level);
        }
    }

    public static boolean initializeLogger() {
        return initializeLogger(false);
    }

    public static boolean initializeLogger(boolean shortLog) {
        String properties = shortLog ? logging_properties_short : logging_properties;
        InputStream config = ClassLoader.getSystemResourceAsStream(properties);
        if (config == null) {
            System.err.println("Failed to initialize logger from resource " + properties);
        } else {
            try {
                LogManager.getLogManager().readConfiguration(config);
                config.close();
                return true;
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to initialize logger from resource " + properties, e);
            }
        }
        return false;
    }

}
