package com.adus.bipartite_doc_matcher.input;

import lombok.Data;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

@Data
public final class ValueType {
    public static final ValueType DOCUMENT = new ValueType(Document.class);
    public static final ValueType ARRAY = new ValueType(Array.class);
    public static final ValueType STRING = new ValueType(String.class);
    public static final ValueType NUMBER = new ValueType(Number.class);

    private final Class<?> typeClazz;

    private ValueType(Class<?> typeClazz) {
        this.typeClazz = typeClazz;
    }

    public static ValueType of(Object value) {
        if (value instanceof String) {
            return ValueType.STRING;
        }
        if (value instanceof Number) {
            return ValueType.NUMBER;
        }
        if (value instanceof Collection || value instanceof ArrayValue) {
            return ValueType.ARRAY;
        }
        if (value instanceof Map || value instanceof Document) {
            return ValueType.DOCUMENT;
        }
        if (value instanceof SimpleValue) {
            return of(((SimpleValue) value).getValue());
        }
        return null;
    }

}
