package metrics;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 *
 * Represents one vector of data.
 * The header contains labels for the values. It does not contain special fields like
 * {@link Header#HEADER_TIME}, although these fields are also transported over the network.
 */
public class Sample {

    private final Date timestamp;
    private final Header header;
    private final double[] metrics;
    private String source;
    private String label;

    public Sample(Header header, double[] metrics, Date timestamp, String source, String label) {
        this.header = header;
        this.timestamp = timestamp;
        this.metrics = metrics;
        this.source = source;
        this.label = label;
    }

    public Sample(Header header, double[] metrics, Date timestamp, String source) {
        this(header, metrics, timestamp, source, null);
    }

    public Sample(Header header, double[] metrics, Date timestamp) {
        this(header, metrics, timestamp, null, null);
    }

    public Sample(Header header, double[] metrics) {
        this(header, metrics, null, null, null);
    }

    public Header getHeader() {
        return header;
    }

    public double[] getMetrics() {
        return metrics;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean hasTimestamp() {
        return header.hasTimestamp() && timestamp != null;
    }

    public String getSource() {
        return source;
    }

    public boolean hasSource() {
        return header.hasSource() && source != null;
    }

    public String getLabel() {
        return label;
    }

    public boolean hasLabel() {
        return header.hasLabel() && label != null;
    }

    public boolean headerChanged(Header oldHeader) {
        return header.hasChanged(oldHeader);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void checkConsistency() throws IOException {
        if (header == null)
            throw new IOException("Sample.header is null");
        if (metrics == null)
            throw new IOException("Sample.metrics is null");
        if (header.header.length != metrics.length)
            throw new IOException("Sample.header is size " + header.header.length +
                    ", but Sample.metrics is size " + metrics.length);
    }

    public static class Header {

        public static final String HEADER_TIME = "time";
        public static final int HEADER_TIME_IDX = 0;
        public static final String HEADER_SOURCE = "source";
        public static final int HEADER_SOURCE_IDX = 1;
        public static final String HEADER_LABEL = "label";
        public static final int HEADER_LABEL_IDX = 2;

        public static final int TOTAL_SPECIAL_FIELDS = 3;

        public static final List<String> SPECIAL_FIELDS = Arrays.asList(
            HEADER_TIME, HEADER_SOURCE, HEADER_LABEL
        );

        private static boolean isSpecial(String field) {
            for (String special : SPECIAL_FIELDS) {
                if (special.equals(field)) {
                    return true;
                }
            }
            return false;
        }

        public final String[] header;
        public int specialFields;

        public Header(String[] fullHeader) {
            int specialFields = 0;
            for (int i = 0; i < SPECIAL_FIELDS.size() && i < fullHeader.length; i++) {
                if (fullHeader[i].equals(SPECIAL_FIELDS.get(i))) {
                    specialFields++;
                } else {
                    break;
                }
            }
            this.specialFields = specialFields;
            if (specialFields == 0) {
                header = fullHeader;
                return;
            }
            header = new String[fullHeader.length - specialFields];
            System.arraycopy(fullHeader, specialFields, header, 0, header.length);
        }

        // Copy the special fields from oldHeader, but replace the regular fields with newHeader.
        // newHeader only contains non-special fields (unlike fullHeader in the other constructur)
        public Header(Header oldHeader, String[] newHeader) {
            this.specialFields = oldHeader.specialFields;
            this.header = newHeader;
        }

        public Header(String[] header, int numSpecialFields) {
            this.specialFields = numSpecialFields;
            this.header = header;
        }

        public void ensureSpecialField(int fieldIndex) {
            if (specialFields < fieldIndex + 1) {
                specialFields = fieldIndex + 1;
            }
        }

        public String[] getSpecialFields() {
            String result[] = new String[specialFields];
            for (int i = 0; i < specialFields; i++) {
                result[i] = SPECIAL_FIELDS.get(i);
            }
            return result;
        }

        public int numFields() {
            return header.length + specialFields;
        }

        public boolean hasTimestamp() {
            return specialFields >= HEADER_TIME_IDX + 1;
        }

        public boolean hasSource() {
            return specialFields >= HEADER_SOURCE_IDX + 1;
        }

        public boolean hasLabel() {
            return specialFields >= HEADER_LABEL_IDX + 1;
        }

        public boolean hasChanged(Header oldHeader) {
            if (oldHeader == null || numFields() != oldHeader.numFields()) {
                return true;
            } else if (this != oldHeader) {
                if (specialFields != oldHeader.specialFields) {
                    return true;
                }
                // New instance with same length: must compare all header fields. Rare case.
                for (int i = 0; i < header.length; i++) {
                    if (!header[i].equals(oldHeader.header[i])) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    public static Sample newEmptySample() {
        return new Sample(new Sample.Header(new String[0]), new double[0]);
    }

}
