package bitflow4j.script.registry;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisteredParameter {

    private static final Map<Class<?>, Parser> primitiveParsers = new HashMap<>();

    static {
        primitiveParsers.put(String.class, (x) -> x);
        primitiveParsers.put(Double.class, Double::parseDouble);
        primitiveParsers.put(double.class, Double::parseDouble);
        primitiveParsers.put(Long.class, Long::parseLong);
        primitiveParsers.put(long.class, Long::parseLong);
        primitiveParsers.put(Float.class, Float::parseFloat);
        primitiveParsers.put(float.class, Float::parseFloat);
        primitiveParsers.put(Integer.class, Integer::parseInt);
        primitiveParsers.put(int.class, Integer::parseInt);
        primitiveParsers.put(Boolean.class, Boolean::parseBoolean);
        primitiveParsers.put(boolean.class, Boolean::parseBoolean);
    }

    public interface Parser {
        Object parse(String str);
    }

    public enum ContainerType {
        Primitive, List, Map, Array
    }

    public final String name;
    public final ContainerType containerType;
    public final Class<?> type;

    public RegisteredParameter(String name, ContainerType containerType, Class<?> type) {
        this.name = name;
        this.containerType = containerType;
        this.type = type;
    }

    public RegisteredParameter(Parameter param) throws IllegalArgumentException {
        this.name = param.getName();
        Type paramType = param.getParameterizedType();

        Class<?> primitive = getPrimitiveType(paramType);
        Class<?> list = getListType(paramType);
        Class<?> array = getArrayType(paramType);
        Class<?> map = getMapType(paramType);
        if (primitive != null) {
            type = primitive;
            containerType = ContainerType.Primitive;
        } else if (list != null) {
            type = list;
            containerType = ContainerType.List;
        } else if (array != null) {
            type = array;
            containerType = ContainerType.Array;
        } else if (map != null) {
            type = map;
            containerType = ContainerType.Map;
        } else {
            throw new IllegalArgumentException(String.format("Cannot construct registered parameter from type %s", paramType));
        }
    }

    public String toString() {
        return String.format("%s (type %s)", name, typeString());
    }

    public String typeString() {
        switch (containerType) {
            case Primitive:
                return type.getSimpleName();
            case List:
                return String.format("List (%s)", type.getSimpleName());
            case Array:
                return String.format("Array (%s)", type.getSimpleName());
            case Map:
                return String.format("Map (String => %s)", type.getSimpleName());
        }
        return String.format("Unknown container type %s of %s", containerType, type);
    }

    public static boolean isParseable(Type paramType) {
        return getPrimitiveType(paramType) != null || getMapType(paramType) != null || getListType(paramType) != null || getArrayType(paramType) != null;
    }

    public static Class<?> getPrimitiveType(Type paramType) {
        if (paramType instanceof Class && primitiveParsers.containsKey(paramType)) {
            return (Class) paramType;
        }
        return null;
    }

    public static Class<?> getMapType(Type paramType) {
        if (paramType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) paramType;
            Type[] typeArgs = type.getActualTypeArguments();
            if (type.getRawType() == Map.class && typeArgs.length == 2 && typeArgs[0] == String.class && getPrimitiveType(typeArgs[1]) != null) {
                return (Class) typeArgs[1];
            }
        }
        return null;
    }

    public static Class<?> getListType(Type paramType) {
        if (paramType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) paramType;
            Type[] typeArgs = type.getActualTypeArguments();
            if (type.getRawType() == List.class && typeArgs.length == 1 && getPrimitiveType(typeArgs[0]) != null) {
                return (Class) typeArgs[0];
            }
        }
        return null;
    }

    public static Class<?> getArrayType(Type paramType) {
        if (paramType instanceof Class) {
            Class paramClass = (Class) paramType;
            if (paramClass.isArray()) {
                Type componentType = paramClass.getComponentType();
                if (getPrimitiveType(componentType) != null)
                    return (Class) componentType;
            }
        }
        return null;
    }

    public boolean canParse(Object inputValue) {
        return (inputValue instanceof Map && containerType == ContainerType.Map)
                || (inputValue instanceof List && (containerType == ContainerType.List || containerType == ContainerType.Array))
                || (inputValue instanceof String && containerType == ContainerType.Primitive);
    }

    /**
     * @param inputValue The input value can be of type String, Map or List.
     */
    public Object parseValue(Object inputValue) throws IllegalArgumentException {
        if (inputValue instanceof Map) {
            return parseMapValue((Map) inputValue);
        } else if (inputValue instanceof List) {
            return parseListOrArrayValue((List) inputValue);
        } else if (inputValue instanceof String) {
            return parsePrimitiveValue((String) inputValue);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported value type of parameter %s (%s): %s",
                    name, inputValue.getClass(), inputValue));
        }
    }

    public Object parsePrimitiveValue(String strValue) throws IllegalArgumentException {
        if (containerType != ContainerType.Primitive) {
            throw new IllegalArgumentException(String.format("Parameter %s received primitive value: %s", this, strValue));
        }
        return parsePrimitiveValue(strValue, "");
    }

    private Object parsePrimitiveValue(String strValue, String messageSuffix) throws IllegalArgumentException {
        Parser parser = primitiveParsers.get(type);
        if (parser == null) {
            throw new IllegalArgumentException(String.format("Parameter %s%s contains unregistered primitive type %s", this, messageSuffix, type));
        } else {
            try {
                return parser.parse(strValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Parameter %s%s failed to parse value '%s': %s",
                        this, messageSuffix, strValue, e));
            }
        }
    }

    public Object parseListOrArrayValue(List<?> inputList) throws IllegalArgumentException {
        if (containerType == ContainerType.Array) {
            return parseArrayValue(inputList);
        } else if (containerType == ContainerType.List) {
            return parseListValue(inputList);
        } else {
            throw new IllegalArgumentException(String.format("Parameter %s received list value: %s", this, inputList));
        }
    }

    public List<?> parseListValue(List<?> inputList) throws IllegalArgumentException {
        List<Object> parsedList = new ArrayList<>(inputList.size());
        for (int i = 0; i < inputList.size(); i++) {
            Object arg = inputList.get(i);
            if (!(arg instanceof String)) {
                throw new IllegalArgumentException(
                        String.format("Element %s in list-parameter %s is of type %s (expecting only String): %s",
                                i, this, arg.getClass(), arg));
            }
            Object parsedVal = parsePrimitiveValue((String) arg, " (list element " + i + ")");
            parsedList.add(parsedVal);
        }
        return parsedList;
    }

    public Object parseArrayValue(List<?> inputList) throws IllegalArgumentException {
        Object parsedArray = Array.newInstance(type, inputList.size());
        for (int i = 0; i < inputList.size(); i++) {
            Object arg = inputList.get(i);
            if (!(arg instanceof String)) {
                throw new IllegalArgumentException(
                        String.format("Element %s in list-parameter %s is of type %s (expecting only String): %s",
                                i, this, arg.getClass(), arg));
            }
            Object parsedVal = parsePrimitiveValue((String) arg, " (list element " + i + ")");
            setArrayElement(i, parsedArray, parsedVal, type);
        }
        return parsedArray;
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static void setArrayElement(int index, Object array, Object element, Class<?> elementType) {

        // TODO HACK could not find a cleaner way to set single element in a generic array, that could also contain primitive values

        if (!elementType.isPrimitive()) {
            Object[] miniArray = new Object[]{element};
            System.arraycopy(miniArray, 0, array, index, 1);
        } else if (elementType == double.class) {
            double[] miniArray = new double[]{(Double) element};
            System.arraycopy(miniArray, 0, array, index, 1);
        } else if (elementType == int.class) {
            int[] miniArray = new int[]{(Integer) element};
            System.arraycopy(miniArray, 0, array, index, 1);
        } else if (elementType == float.class) {
            float[] miniArray = new float[]{(Float) element};
            System.arraycopy(miniArray, 0, array, index, 1);
        } else if (elementType == long.class) {
            long[] miniArray = new long[]{(Long) element};
            System.arraycopy(miniArray, 0, array, index, 1);
        } else if (elementType == boolean.class) {
            boolean[] miniArray = new boolean[]{(Boolean) element};
            System.arraycopy(miniArray, 0, array, index, 1);
        }
    }

    public Map<String, ?> parseMapValue(Map<?, ?> inputMap) throws IllegalArgumentException {
        if (containerType != ContainerType.Map) {
            throw new IllegalArgumentException(String.format("Parameter %s received map value: %s", this, inputMap));
        }

        Map<String, Object> parsedMap = new HashMap<>(inputMap.size());
        for (Object key : inputMap.keySet()) {
            Object value = inputMap.get(key);
            if (!(key instanceof String) || !(value instanceof String)) {
                throw new IllegalArgumentException(
                        String.format("Entry in parameter %s is of type %s = %s (expecting only Strings): %s = %s ",
                                this, key.getClass(), value.getClass(), key, value));
            }
            Object parsedVal = parsePrimitiveValue((String) value, " (map key " + key + ")");
            parsedMap.put((String) key, value);
        }
        return parsedMap;
    }

}
