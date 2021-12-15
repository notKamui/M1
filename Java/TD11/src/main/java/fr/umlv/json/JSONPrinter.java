package fr.umlv.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONPrinter {
    private final static ClassValue<List<Function<Record, String>>> CACHE = new ClassValue<>() {
        @Override
        protected List<Function<Record, String>> computeValue(Class<?> type) {
            return Arrays.stream(type.getRecordComponents()).map(JSONPrinter::makeJson).toList();
        }
    };

    private JSONPrinter() {
    }

    public static String toJSON(Record serializable) {
        return CACHE.get(serializable.getClass()).stream()
            .map(component -> component.apply(serializable))
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

    private static Function<Record, String> makeJson(RecordComponent component) {
        var accessor = component.getAccessor();
        var name = propName(accessor);
        return record -> "\"%s\": %s".formatted(name, escape(invoke(accessor, record)));
    }

    private static String escape(Object o) {
        return o instanceof String
            ? "\"%s\"".formatted(o)
            : o.toString();
    }
}
