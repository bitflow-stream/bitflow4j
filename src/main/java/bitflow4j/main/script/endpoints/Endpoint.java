package bitflow4j.main.script.endpoints;

public class Endpoint {
    private Format format;//MarshallingFormat
    private Type type;//EndpointType
    private String target;

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        if (type == Type.STD && !"-".equals(target)) {
            throw new EndpointParseException("Type 'std' requires target '-', target was "+getTarget());
        }
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return format.toString() + "+" + type.toString() + "://" + target;
    }


    public enum Type {
        TCP, LISTEN, FILE, STD, EMPTY;

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
        UNDEFINED, TEXT, CSV, BINARY, WAV;

        public static Format find(String search) {
            for (Format t : Format.values()) {
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

}
