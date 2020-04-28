package bitflow4j.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class RegisteredParameterList {

    private final Map<String, RegisteredParameter> params = new HashMap<>();

    public RegisteredParameterList() {
        // No parameters
    }

    public RegisteredParameterList(RegisteredParameter[] parameters) {
        Arrays.stream(parameters).forEach(this::add);
    }

    public RegisteredParameterList(Constructor<?> constructor) {
        params.putAll(getConstructorParameters(constructor));
    }

    public Collection<RegisteredParameter> getParams() {
        return params.values();
    }

    public void add(RegisteredParameter param) {
        params.put(param.name, param);
    }

    public static List<String> getParameterNames(Executable methodOrConstructor) {
        return Arrays.stream(methodOrConstructor.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    }

    private static Map<String, RegisteredParameter> getConstructorParameters(Executable constructor) {
        Map<String, RegisteredParameter> result = new HashMap<>();
        for (Parameter param : constructor.getParameters()) {
            RegisteredParameter registeredParam = new RegisteredParameter(param);
            result.put(registeredParam.name, registeredParam);
        }
        return result;
    }

    /**
     * validate takes a map of parameters and validates them against the specified optional and required parameters.
     *
     * @param params the input parameters to be validated
     * @return an error description in the specified input parameters (required but missing or unexpected)
     */
    public String validate(Map<String, Object> params) {
        List<String> errors = new ArrayList<>();
        params.keySet().stream()
                .map(k -> validateParam(k, params.get(k)))
                .filter(Objects::nonNull)
                .forEach(errors::add);
        this.params.entrySet().stream()
                .filter(s -> !s.getValue().isOptional && !params.containsKey(s.getKey()))
                .map(s -> "Missing required parameter '" + s + "'")
                .forEach(errors::add);
        if (!errors.isEmpty())
            return String.join("\n", errors);
        else
            return null;
    }

    private String validateParam(String name, Object value) {
        RegisteredParameter param = params.get(name);
        if (param == null)
            return "Unexpected parameter " + name;
        if (!param.canParse(value))
            return "Parameter " + param + " received wrong value type";
        return null;
    }

    public Map<String, Object> parseRawParameters(Map<String, Object> rawParameters) throws IllegalArgumentException {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, RegisteredParameter> paramEntry : params.entrySet()) {
            RegisteredParameter param = paramEntry.getValue();
            String name = paramEntry.getKey();
            if (rawParameters.containsKey(name)) {
                Object parsedValue = param.parseValue(rawParameters.get(name));
                result.put(name, parsedValue);
            } else if (param.isOptional) {
                result.put(name, param.getDefaultValue());
            } else {
                throw new IllegalArgumentException("Missing required parameter " + param);
            }
        }
        for (String name : rawParameters.keySet()) {
            if (!params.containsKey(name)) {
                throw new IllegalArgumentException("Unknown parameter: " + name);
            }
        }
        return result;
    }

}
