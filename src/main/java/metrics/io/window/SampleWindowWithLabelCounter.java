package metrics.io.window;

import metrics.Sample;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by malcolmx on 30.08.16.
 */
public class SampleWindowWithLabelCounter extends SampleWindow {
    private Map<String, Integer> countsPerLabel = new HashMap<>();
    public SampleWindowWithLabelCounter() {
        super();
    }

    public SampleWindowWithLabelCounter(int windowSize) {
        super(windowSize);
    }

    public SampleWindowWithLabelCounter(long windowTimespan) {
        super(windowTimespan);
    }

    @Override
    boolean addSample(Sample sample) {
        String label = sample.getLabel();
        if(label == null) ;//TODO handle the null label
        if (countsPerLabel.containsKey(label)){
            countsPerLabel.put(label, countsPerLabel.get(label) + 1);
        }else countsPerLabel.put(label, 1);
        return super.addSample(sample);
    }

    public int getCountsForLabel(String label){
        if (countsPerLabel.containsKey(label)) return countsPerLabel.get(label);
        else return -1;
    }

    public Map<String, Integer> getCountsPerLabel(){
        return countsPerLabel;
    }
}
