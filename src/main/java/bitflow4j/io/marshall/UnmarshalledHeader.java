package bitflow4j.io.marshall;

import bitflow4j.Header;

/**
 * Created by anton on 20.03.17.
 */
public class UnmarshalledHeader {

    public final Header header;
    public final boolean hasTags;

    public UnmarshalledHeader(Header header, boolean hasTags) {
        this.header = header;
        this.hasTags = hasTags;
    }

}
