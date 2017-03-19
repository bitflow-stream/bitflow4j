package bitflow4j.sample;

/**
 * Created by anton on 5/3/16.
 * <p>
 * Header of a Sample. Subsequent incoming Samples should use the same Header instance as long as the Header does not
 * change. The header defines a String array of field names, plus an additional boolean flag indicating whether
 * the associated samples contain tags or not.
 */
public class Header {

    public static final Header EMPTY_HEADER = new Header(new String[0], true);

    public final String[] header;
    public final boolean hasTags;

    public Header(String[] header, Header oldHeader) {
        this(header, header.length > 0);
    }

    public Header(String[] header) {
        this(header, true);
    }

    public Header(String[] header, boolean hasTags) {
        this.header = header;
        this.hasTags = hasTags;
    }

    public boolean hasChanged(Header oldHeader) {
        if (oldHeader == null || header.length != oldHeader.header.length) {
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
