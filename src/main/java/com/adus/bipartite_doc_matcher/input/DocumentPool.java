package com.adus.bipartite_doc_matcher.input;

import com.adus.bipartite_doc_matcher.core.MatchableDocument;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentPool {
    private final List<Document> lhsDocuments;
    private final List<Document> rhsDocuments;

    public DocumentPool(List<Document> lhsDocuments, List<Document> rhsDocuments) {
        this.lhsDocuments = lhsDocuments;
        this.rhsDocuments = rhsDocuments;
    }

    public List<MatchableDocument> getLhsMatchableDocuments() {
        return lhsDocuments.stream().map(MatchableDocument::new).collect(Collectors.toList());
    }

    public List<MatchableDocument> getRhsMatchableDocuments() {
        return rhsDocuments.stream().map(MatchableDocument::new).collect(Collectors.toList());
    }
}
