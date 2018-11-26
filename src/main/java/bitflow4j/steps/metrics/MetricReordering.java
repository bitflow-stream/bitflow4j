package bitflow4j.steps.metrics;

import bitflow4j.Header;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 23.11.16.
 */
public class MetricReordering extends AbstractMetricReordering {

    private static final Logger logger = Logger.getLogger(MetricReordering.class.getName());

    private final String[] requestedHeaderFields;

    public MetricReordering(String... headerFields) {
        this.requestedHeaderFields = headerFields;
    }

    @Override
    protected int[] newHeaderIndices(Header inHeader) {
        int indices[] = new int[requestedHeaderFields.length];
        Map<String, Integer> fieldMap = new HashMap<>();
        for (int i = 0; i < inHeader.header.length; i++) {
            fieldMap.put(inHeader.header[i], i);
        }

        for (int i = 0; i < requestedHeaderFields.length; i++) {
            String field = requestedHeaderFields[i];
            if (fieldMap.containsKey(field)) {
                indices[i] = fieldMap.get(field);
            } else {
                logger.warning("Field " + field + " not found in incoming header, setting values to 0.");
                indices[i] = -1;
            }
        }
        return indices;
    }

}
