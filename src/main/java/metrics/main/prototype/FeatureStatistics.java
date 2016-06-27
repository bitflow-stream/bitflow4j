package metrics.main.prototype;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/26/16.
 */
public class FeatureStatistics {

    public static class Feature {
        public final String name;
        public final double min;
        public final double max;
        public final double avg;
        public final double stddev;
        public final int count;

        public Feature(String name, double min, double max, double avg, double stddev, int count) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.avg = avg;
            this.stddev = stddev;
            this.count = count;
        }

        public Feature(String name, Map<String, String> values) throws NumberFormatException {
            this.name = name;
            this.min = parseDouble(values, "min");
            this.max = parseDouble(values, "max");
            this.avg = parseDouble(values, "avg");
            this.stddev = parseDouble(values, "stddev");
            this.count = parseInt(values, "count");
        }

        private double parseDouble(Map<String, String> values, String key) throws NumberFormatException {
            String val = values.get(key);
            if (val == null || val.isEmpty()) {
                throw new NumberFormatException("Empty or missing key '" + key + "' for feature " + name +
                        ". Available keys: " + values);
            }
            try {
                return Double.parseDouble(val);
            } catch(NumberFormatException exc) {
                throw new NumberFormatException("Failed to parse key '" + key + "' for feature " + name + ": " + exc.toString());
            }
        }

        private int parseInt(Map<String, String> values, String key) throws NumberFormatException {
            String val = values.get(key);
            if (val == null || val.isEmpty()) {
                throw new NumberFormatException("Empty or missing key '" + key + "' for feature " + name +
                        ". Available keys: " + values);
            }
            try {
                return Integer.parseInt(val);
            } catch(NumberFormatException exc) {
                throw new NumberFormatException("Failed to parse key '" + key + "' for feature " + name + ": " + exc.toString());
            }
        }

        public String toString() {
            return name + " (min: " + min + ", max: " + max + ", avg: " + avg +
                    ", stddev: " + stddev + ", count: " + count + ")";
        }
    }

    private final Map<String, Feature> features = new HashMap<>();

    public FeatureStatistics(String iniFile) throws IOException {
        Ini ini = new Ini();
        ini.getConfig().setTree(false);
        ini.load(new File(iniFile));
        fillFromIni(ini);
    }

    public Feature getFeature(String name) {
        return features.get(name);
    }

    public Collection<String> allFeatureNames() {
        return features.keySet();
    }

    public Collection<Feature> allFeatures() {
        return features.values();
    }

    private void fillFromIni(Ini ini) {
        features.clear();
        for (Map.Entry<String, Profile.Section> entry : ini.entrySet()) {
            String featureName = entry.getKey();
            Feature feature = new Feature(featureName, entry.getValue());
            features.put(featureName, feature);
        }
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.err.println("Parameters: <ini-file>");
            System.exit(1);
        }
        String file = args[0];
        FeatureStatistics stats = new FeatureStatistics(file);
        System.err.println("Number of features: " + stats.allFeatures().size());
        for (Feature feature : stats.allFeatures()) {
            System.err.println(feature);
        }
    }

}
