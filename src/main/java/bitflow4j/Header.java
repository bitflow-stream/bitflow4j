package bitflow4j;

/**
 * Created by anton on 5/3/16.
 * <p>
 * Header of a Sample. Subsequent incoming Samples should use the same Header instance as long as the Header does not
 * change.
 */
public class Header {

    public static final String HEADER_TIME = "time";
    public static final String HEADER_TAGS = "tags";

    public static final Header EMPTY_HEADER = new Header(new String[0], true);

    public final String[] header;
    public final boolean hasTags;

    public Header(String[] header, Header oldHeader) {
        this(header, oldHeader.hasTags);
    }

    public Header(String[] header) {
        this(header, true);
    }

    public Header(String[] header, boolean hasTags) {
        this.header = header;
        this.hasTags = hasTags;
    }

    public static Header unmarshallHeader(String fields[]) {
        if (fields.length < 1 || !fields[0].equals(HEADER_TIME)) {
            throw new IllegalArgumentException("First field in header must be " + HEADER_TIME);
        }
        boolean hasTags = fields.length >= 2 && fields[1].equals(HEADER_TAGS);

        int specialFields = hasTags ? 2 : 1;
        String header[] = new String[fields.length - specialFields];
        System.arraycopy(fields, specialFields, header, 0, header.length);
        return new Header(header, hasTags);
    }

    public int numSpecialFields() {
        return hasTags ? 2 : 1;
    }

    public String[] getSpecialFields() {
        String result[] = new String[numSpecialFields()];
        result[0] = HEADER_TIME;
        if (hasTags) result[1] = HEADER_TAGS;
        return result;
    }

    public int numFields() {
        return header.length + numSpecialFields();
    }

    public boolean hasChanged(Header oldHeader) {
        if (oldHeader == null || numFields() != oldHeader.numFields()) {
            return true;
        } else if (this != oldHeader) {
            if (hasTags != oldHeader.hasTags) {
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

    public boolean equals(Object other) {
        return other instanceof Header && !hasChanged((Header) other);
    }

}
