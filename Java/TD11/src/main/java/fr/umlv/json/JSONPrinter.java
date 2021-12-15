package fr.umlv.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class JSONPrinter {
    private JSONPrinter() {
    }

    public static String toJSON(Record serializable) {
        return Arrays.stream(serializable
            .getClass()
            .getRecordComponents())
            .map(component -> {
                var accessor = component.getAccessor();
                var prop = component.getAnnotation(JSONProperty.class);
                var name = prop == null ?
                    accessor.getName().replaceAll("_", "-")
                    : prop.value();
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

    private static String escape(Object o) {
        return o instanceof String
            ? "\"%s\"".formatted(o)
            : o.toString();
    }
}
