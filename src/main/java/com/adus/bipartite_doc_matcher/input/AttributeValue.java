package com.adus.bipartite_doc_matcher.input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// todo: add compressibility feature
public interface AttributeValue {
    /**
     * Specified how this value should get matched with other values.
     * todo: add match-mode support for all types of values
     *
     * @return match-mode
     */
    @Nonnull
    MatchMode matchMode();

    @Nonnull
    ValueType valueType();

    @Nullable
    Document getAsDocumentValue();

    @Nullable
    SimpleValue getAsSimpleValue();

    @Nullable
    ArrayValue getAsArrayValue();
}
