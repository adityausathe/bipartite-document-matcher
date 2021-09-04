package com.adus.bipartite_doc_matcher.input;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class CompressionUtil {
    private static final String COMPRESSION_DELIMITER = " | ";

    public static String compress(Document document) {
        return document.getAttributes()
                .entrySet()
                .stream()
                .map(attr -> joinKV(attr.getKey(), attr.getValue().getAsSimpleValue().getValue()))
                .collect(Collectors.joining(COMPRESSION_DELIMITER));
    }

    public static String compress(ArrayValue arrayValue) {
        return Arrays.stream(arrayValue.getElements())
                .map(element -> element.getAsSimpleValue().getValue().toString())
                .collect(Collectors.joining(COMPRESSION_DELIMITER));
    }

    private static String joinKV(String key, Object value) {
        return key + "[" + (value == null ? "" : value.toString()) + "]";
    }
}
