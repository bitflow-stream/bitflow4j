package metrics.main;

import weka.classifiers.trees.J48;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * IMPORTANT: This class must stay in this package to make use of previously serialized instances.
 *
 * Created by anton on 6/14/16.
 */
public class TrainedDataModel implements Serializable {
    public J48 model;
    public Map<String, Double> averages;
    public Map<String, Double> stddevs;
    public String[] headerFields;
    public ArrayList<String> allClasses;
}
