package bitflow4j.script.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class RegisteredParameterList {

    private final Map<String, RegisteredParameter> optional = new HashMap<>();
    private final Map<String, RegisteredParameter> required = new HashMap<>();

    public RegisteredParameterList() {
        // No parameters
    }

    public RegisteredParameterList(RegisteredParameter[] defaultRequired, RegisteredParameter[] defaultOptional) {
        Arrays.stream(defaultRequired).forEach(p -> add(p, true));
        Arrays.stream(defaultOptional).forEach(p -> add(p, false));
    }

    public RegisteredParameterList(Collection<Constructor<?>> constructors) {
        fillFromConstructors(constructors);
    }

    public Collection<RegisteredParameter> getOptional() {
        return optional.values();
    }

    public Collection<RegisteredParameter> getRequired() {
        return required.values();
    }

    public void add(RegisteredParameter param, boolean isRequired) {
        if (isRequired)
            required.put(param.name, param);
        else
            optional.put(param.name, param);
    }

    public static List<String> getParameterNames(Executable methodOrConstructor) {
        return Arrays.stream(methodOrConstructor.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    }

    private void fillFromConstructors(Collection<Constructor<?>> constructors) {
        if (constructors.isEmpty())
            return;

        Map<String, RegisteredParameter> newOptional = new HashMap<>();
        Map<String, RegisteredParameter> newRequired = getConstructorParameters(constructors.iterator().next());

        constructors.forEach(constructor -> {
            Map<String, RegisteredParameter> params = getConstructorParameters(constructor);

            // Every new parameter is registered optional
            for (RegisteredParameter param : params.values()) {
                if (!newRequired.containsKey(param.name)) {
                    newOptional.put(param.name, param);
                }
            }

            // Clean requiredParams by moving extraneous parameters from requiredParams to optionalParams
            for (Iterator<String> i = newRequired.keySet().iterator(); i.hasNext(); ) {
                String name = i.next();
                if (!params.containsKey(name)) {
                    newOptional.put(name, newRequired.get(name));
                    i.remove();
                }
            }
        });
        optional.putAll(newOptional);
        required.putAll(newRequired);
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
     * validateParameters takes a map of parameters and validates them against the specified optional and required parameters.
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
        required.keySet().stream()
                .filter(s -> !params.containsKey(s))
                .map(s -> "Missing required parameter '" + s + "'")
                .forEach(errors::add);
        if (!errors.isEmpty())
            return String.join("\n", errors);
        else
            return null;
    }

    private String validateParam(String name, Object value) {
        RegisteredParameter param = optional.get(name);
        if (param == null)
            param = required.get(name);
        if (param == null)
            return "Unexpected parameter " + name;
        if (!param.canParse(value))
            return "Parameter " + param + " received wrong value type";
        return null;
    }

    public Map<String, Object> parseRawParameters(Map<String, Object> rawParameters) throws IllegalArgumentException {
        for (String required : required.keySet()) {
            if (!rawParameters.containsKey(required)) {
                throw new IllegalArgumentException("Missing required parameter " + required);
            }
        }

        Map<String, Object> result = new HashMap<>();
        for (String name : rawParameters.keySet()) {
            Object rawValue = rawParameters.get(name);
            if (required.containsKey(name)) {
                result.put(name, required.get(name).parseValue(rawValue));
            } else if (optional.containsKey(name)) {
                result.put(name, optional.get(name).parseValue(rawValue));
            } else {
                throw new IllegalArgumentException("Unknown parameter: " + name);
            }
        }
        return result;
    }

}
