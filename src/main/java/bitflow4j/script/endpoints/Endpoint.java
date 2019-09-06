package bitflow4j.script.endpoints;

import bitflow4j.io.marshall.*;

/**
 * Endpoint contains the parsed information of an endpoint token
 */
public class Endpoint {

    private final String endpointToken;
    private final Format format;
    private final Type type;
    private final String target;
    private final String customType;

    public Endpoint(String endpointToken, String target, Format format, Type type, String customType) {
        this.endpointToken = endpointToken;
        this.target = target;
        this.type = type;
        this.format = format;
        this.customType = customType;
    }

    public Endpoint(String endpointToken, String target, Format format, Type type) {
        this(endpointToken, target, format, type, "");
    }

    public Endpoint(String endpointToken, String target, String customType) {
        this(endpointToken, target, Format.UNDEFINED, Type.CUSTOM, customType);
    }

    public Format getFormat() {
        return format;
    }

    public Type getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getCustomType() {
        return customType;
    }

    @Override
    public String toString() {
        return endpointToken;
    }

    public Marshaller getMarshaller() {
        return getFormat().getMarshaller();
    }

    public enum Type {
        TCP, LISTEN, FILE, STD, EMPTY, CUSTOM;

        public static Type find(String search) {
            for (Type t : Type.values()) {
                if (t.name().compareToIgnoreCase(search) == 0) {
                    return t;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Format {
        UNDEFINED, TEXT, CSV, BIN, WAV;

        public static Format find(String search) {
            for (Format t : Format.values()) {
                if (t.name().compareToIgnoreCase(search) == 0) {
                    return t;
                }
            }
            return null;
        }

        public Marshaller getMarshaller() {
            switch (this) {
                case BIN:
                    return new BinaryMarshaller();
                case CSV:
                    return new CsvMarshaller();
                case WAV:
                    return new WavAudioMarshaller();
                case TEXT:
                    return new TextMarshaller();
                default:
                    throw new EndpointParseException(toString(), "Could not find a Marshaller for specified format " + this);
            }
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
