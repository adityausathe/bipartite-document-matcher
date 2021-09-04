package com.adus.bipartite_doc_matcher.input;

import lombok.Data;

@Data
public final class MatchMode {
    public static final MatchMode HASH = new MatchMode("HASH");
    public static final MatchMode NORM = new MatchMode("NORM");
    private final String name;

    private MatchMode(String name) {
        this.name = name;
    }
}
