package metrics.main;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by anton on 4/14/16.
 */
public class Config {

    public final String experimentFolder;
    public final String oldExperimentFolder;
    public final String outputFolder;
    public final String outputFile;

    public Config() {
        this("config");
    }

    public Config(String name) throws MissingResourceException {
        ResourceBundle resources = ResourceBundle.getBundle(name);

        experimentFolder = resources.getString("experiment_dir");
        oldExperimentFolder = resources.getString("old_experiment_dir");
        outputFolder = resources.getString("output_dir");
        outputFile = outputFolder + "/output.csv";
    }

}
