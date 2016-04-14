package metrics.main;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by anton on 4/14/16.
 */
public class Config {

    static final String resource_bundle_name = "config";
    static ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle(resource_bundle_name);
        } catch (MissingResourceException exc) {
            System.err.println("ResourceBundle '" + resource_bundle_name + "' was not found," +
                    "make sure config.properties exists inside src/main/resources");
            System.exit(1);
        }
    }

    static final String EXPERIMENT_FOLDER = resources.getString("experiment_dir");
    static final String OUTPUT_FOLDER = resources.getString("output_dir");
    static final String OUTPUT_FILE = OUTPUT_FOLDER + "/output.csv";

}
