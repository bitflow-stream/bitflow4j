package bitflow4j;

/**
 * Created by anton on 5/3/16.
 * <p>
 * Header of a Sample. Subsequent incoming Samples should use the same Header instance as long as the Header does not
 * change. The header defines a String array of field names.
 */
public class Header {

    public static final Header EMPTY_HEADER = newEmptyHeader();

    public final String[] header;

    public Header(String[] header) {
        this.header = header;
    }

    public boolean hasChanged(Header oldHeader) {
        if (oldHeader == null || header.length != oldHeader.header.length) {
            return true;
        } else if (this != oldHeader) {
            // New instance with same length: must compare all header fields. Rare case.
            for (int i = 0; i < header.length; i++) {
                if (!header[i].equals(oldHeader.header[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public int numFields() {
        return header.length;
    }

    public boolean equals(Object other) {
        return other instanceof Header && !hasChanged((Header) other);
    }

    public static Header newEmptyHeader() {
        return new Header(new String[0]);
    }

}
