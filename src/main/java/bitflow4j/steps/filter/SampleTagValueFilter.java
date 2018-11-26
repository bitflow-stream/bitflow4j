package bitflow4j.steps.filter;

import bitflow4j.Sample;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class SampleTagValueFilter implements SampleFilter.Filter {

    private final Operation operationMode;
    //private final Map<String, String> targetSet;
    private final List<Pair<String, String>> targetSet;

    public enum Operation {
        EXCLUDE_ALL, EXCLUDE_ANY, INCLUDE_ALL, INCLUDE_ANY;
    }

    public SampleTagValueFilter(List<Pair<String, String>> targetSet, Operation operationMode) {
        this.targetSet = targetSet;
        this.operationMode = operationMode;
    }

    @Override
    public boolean shouldInclude(Sample sample) {
        switch (operationMode) {
            case INCLUDE_ANY:
                for (Map.Entry<String, String> sampleTag : sample.getTags().entrySet()) {
                    for (Pair<String, String> targetTag : targetSet) {
                        if (sampleTag.getKey().equals(targetTag.getKey()) && sampleTag.getValue().equals(targetTag.getValue())) {
                            return true;
                        }
                    }
                }
                return false;
            case EXCLUDE_ANY:
                for (Map.Entry<String, String> sampleTag : sample.getTags().entrySet()) {
                    for (Pair<String, String> targetTag : targetSet) {
                        if (sampleTag.getKey().equals(targetTag.getKey()) && sampleTag.getValue().equals(targetTag.getValue())) {
                            return false;
                        }
                    }
                }
                return true;
            case INCLUDE_ALL:
                for (Pair<String, String> targetTag : targetSet) {
                    boolean included = false;
                    for (Map.Entry<String, String> sampleTag : sample.getTags().entrySet()) {
                        if (sampleTag.getKey().equals(targetTag.getKey()) && sampleTag.getValue().equals(targetTag.getValue())) {
                            included= true;
                        }
                    }
                    if(!included){
                        return false;
                    }
                }
                return true;
            case EXCLUDE_ALL:
                for (Pair<String, String> targetTag : targetSet) {
                    boolean included = false;
                    for (Map.Entry<String, String> sampleTag : sample.getTags().entrySet()) {
                        if (sampleTag.getKey().equals(targetTag.getKey()) && sampleTag.getValue().equals(targetTag.getValue())) {
                            included= true;
                        }
                    }
                    if(!included){
                        return true;
                    }
                }
                return false;
        }
        return true;
    }
}
