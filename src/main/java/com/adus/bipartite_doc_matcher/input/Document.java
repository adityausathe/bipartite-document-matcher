package com.adus.bipartite_doc_matcher.input;


import com.adus.bipartite_doc_matcher.common.IdAware;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Document implements AttributeValue, IdAware {
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String id;
    private final Map<String, AttributeValue> attributes;

    public Document(String id) {
        this.id = id;
        this.attributes = new LinkedHashMap<>();
    }

    public Document addAttribute(String name, AttributeValue value) {
        attributes.put(name, value);
        return this;
    }

    public Map<String, AttributeValue> getAttributes() {
        return new LinkedHashMap<>(attributes);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public MatchMode matchMode() {
        return null;
    }

    @Nonnull
    @Override
    public ValueType valueType() {
        return ValueType.DOCUMENT;
    }

    @Override
    public Document getAsDocumentValue() {
        return this;
    }

    @Override
    public SimpleValue getAsSimpleValue() {
        return new SimpleValue(ValueType.STRING, MatchMode.HASH, CompressionUtil.compress(this));
    }

    @Nullable
    @Override
    public ArrayValue getAsArrayValue() {
        return null;
    }

}
