package com.adus.bipartite_doc_matcher.input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// todo: add compressibility feature and math-mode support for Document and Array-type values
public interface AttributeValue {

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
