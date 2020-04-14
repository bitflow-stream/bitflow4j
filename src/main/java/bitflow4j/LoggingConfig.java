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

    static final String logging_properties = "logging.properties";

    public static void setDefaultLogLevel(Level level) {
        Logger root = Logger.getLogger("");
        root.setLevel(level);
        for (Handler h : root.getHandlers()) {
            h.setLevel(level);
        }
    }

    public static void safeInitializeLogger() {
        try {
            initializeLogger();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logging", e);
        }
    }

    public static void initializeLogger() throws IOException {
        InputStream config = ClassLoader.getSystemResourceAsStream(logging_properties);
        if (config == null) {
            System.err.println("Failed to initialize logger from resource " + logging_properties);
        } else {
            LogManager.getLogManager().readConfiguration(config);
            config.close();
        }
    }

}
