package bitflow4j.main.script.antlr;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GenericStateMapTest {
    private static final Stack<Object> emptyStack = new Stack<>();
    private Map<String, Stack<Object>> stackMap = new HashMap<>();

    public <T> T popOrDefault(String key, T defaultValue) {
        Stack<Object> res = stackMap.get(key);
        if (res == null || res.empty()) {
            return defaultValue;
        }
        return (T) res.pop();
    }

    public <T> T pop(String key) {
        Stack<Object> res = stackMap.get(key);
        if (res == null || res.empty()) {
            return null;
        }
        return (T) res.pop();
    }

    public <T> T peek(String key) {
        Stack<Object> res = stackMap.get(key);
        if (res == null || res.empty()) {
            return null;
        }
        return (T) res.peek();
    }

    public <T> T peekOrDefault(String key, T defaultValue) {
        Stack<Object> res = stackMap.get(key);
        if (res == null || res.empty()) {
            return defaultValue;
        }
        return (T) res.peek();
    }

    public void push(String key, Object value) {
        Stack<Object> stack = stackMap.computeIfAbsent(key, k -> new Stack<>());
        stack.push(value);
    }

    public int len(String key) {
        Stack<Object> stack  = stackMap.getOrDefault(key,emptyStack);
        return stack.size();
    }
}
