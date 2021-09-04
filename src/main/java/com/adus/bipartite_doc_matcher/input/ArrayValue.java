package com.adus.bipartite_doc_matcher.input;

import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@Data
public class ArrayValue implements AttributeValue {
    // array of single ValueType only, hence remember elementType at this level instead of checking for each element
    private final ValueType elementType;
    private final AttributeValue[] elements;

    public ArrayValue(ValueType elementType, AttributeValue[] elements) {
        Objects.requireNonNull(elementType);
        this.elementType = elementType;
        this.elements = elements;
    }

    @Nonnull
    @Override
    public MatchMode matchMode() {
        return null;
    }

    @Nonnull
    @Override
    public ValueType valueType() {
        return ValueType.ARRAY;
    }

    @Nullable
    @Override
    public Document getAsDocumentValue() {
        return null;
    }

    @Nullable
    @Override
    public SimpleValue getAsSimpleValue() {
        return new SimpleValue(ValueType.STRING, MatchMode.HASH, CompressionUtil.compress(this));
    }

    @Nullable
    @Override
    public ArrayValue getAsArrayValue() {
        return this;
    }
}
