package fr.umlv.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JSONPrinter {
    private final static HashMap<Class<?>, RecordComponent[]> CLASS_TO_COMPONENTS = new HashMap<>();

    private JSONPrinter() {
    }

    public static String toJSON(Record serializable) {
        return Arrays.stream(getRecordComponents(serializable.getClass()))
            .map(component -> {
                var accessor = component.getAccessor();
                var name = propName(accessor);
                var escape = escape(invoke(accessor, serializable));
                return "\"%s\": %s".formatted(name, escape);
            })
            .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    private static Object invoke(Method accessor, Object object) {
        try {
            return accessor.invoke(object);
        } catch (IllegalAccessException e) {
            throw (IllegalAccessError) new IllegalAccessError().initCause(e);
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException ex) throw ex;
            if (cause instanceof Error ex) throw ex;
            throw new UndeclaredThrowableException(e);
        }
    }

    private static String propName(Method accessor) {
        var name = accessor.getName();
        var prop = accessor.getAnnotation(JSONProperty.class);
        if (prop == null) return name;
        var propName = prop.value();
        return propName.isEmpty()
            ? name.replaceAll("_", "-")
            : propName;
    }

    private static RecordComponent[] getRecordComponents(Class<?> clazz) {
        RecordComponent[] components;
        if (CLASS_TO_COMPONENTS.containsKey(clazz)) {
            components = CLASS_TO_COMPONENTS.get(clazz);
        } else {
            components = clazz.getRecordComponents();
            CLASS_TO_COMPONENTS.put(clazz, components);
        }
        return components;
    }

    private static String escape(Object o) {
        return o instanceof String
            ? "\"%s\"".formatted(o)
            : o.toString();
    }
}
