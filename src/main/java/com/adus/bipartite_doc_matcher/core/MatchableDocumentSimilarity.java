package com.adus.bipartite_doc_matcher.core;

import lombok.Data;

@Data
public class MatchableDocumentSimilarity {
    private final MatchableDocument lhs;
    private final MatchableDocument rhs;
    private final float similarityScore;

    public MatchableDocumentSimilarity(MatchableDocument lhs, MatchableDocument rhs, float similarityScore) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.similarityScore = similarityScore;
    }

    public static MatchableDocumentSimilarity unmatchedSimilarity(MatchableDocument document) {
        return new MatchableDocumentSimilarity(document, null, 0);
    }

    public MatchableDocument getLhs() {
        return lhs;
    }

    public MatchableDocument getRhs() {
        return rhs;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }
}
