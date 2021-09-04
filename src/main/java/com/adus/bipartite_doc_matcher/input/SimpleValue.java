package com.adus.bipartite_doc_matcher.input;

import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@Data
public class SimpleValue implements AttributeValue {
    private final ValueType valueType;
    private final MatchMode matchMode;
    private final Object value;

    public SimpleValue(ValueType valueType, MatchMode matchMode, Object value) {
        Objects.requireNonNull(valueType);
        Objects.requireNonNull(matchMode);
        this.valueType = valueType;
        this.matchMode = matchMode;
        this.value = value;
    }

    public Object getValue() {
        return valueType.getTypeClazz().cast(value);
    }

    @Nonnull
    @Override
    public MatchMode matchMode() {
        return matchMode;
    }

    @Nonnull
    @Override
    public ValueType valueType() {
        return valueType;
    }

    @Nullable
    @Override
    public Document getAsDocumentValue() {
        return null;
    }

    @Nullable
    @Override
    public SimpleValue getAsSimpleValue() {
        return this;
    }

    @Nullable
    @Override
    public ArrayValue getAsArrayValue() {
        return null;
    }
}
