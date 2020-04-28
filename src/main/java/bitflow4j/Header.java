package bitflow4j;

import java.util.Arrays;

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

    public int numFields() {
        return header.length;
    }

    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Header))
            return false;
        Header other = (Header) otherObj;
        return this == other || Arrays.equals(header, other.header);
    }

    public int hashCode() {
        return Arrays.hashCode(header);
    }

    public static Header newEmptyHeader() {
        return new Header(new String[0]);
    }

    public static class ChangeChecker {

        public Header lastHeader = null;

        public boolean changed(Header newHeader) {
            Header lastHeader = this.lastHeader;
            this.lastHeader = newHeader;
            if (lastHeader == null)
                return newHeader != null;
            return !lastHeader.equals(newHeader);
        }

    }

}
