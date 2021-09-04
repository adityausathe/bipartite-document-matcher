package com.adus.bipartite_doc_matcher.output;

import com.adus.bipartite_doc_matcher.input.Document;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MatchResult {
    private final List<DocumentMatch> documentMatches;

    public MatchResult(List<DocumentMatch> documentMatches) {
        this.documentMatches = documentMatches
                .stream()
                .sorted(Comparator.comparing(DocumentMatch::isUnmatched)
                        .thenComparing(Comparator.comparing(DocumentMatch::getSimilarityScore).reversed()))
                .collect(Collectors.toList());
    }

    public String exportAsCsv() {
        StringBuilder csv = new StringBuilder("Matched/Unmatched,LHS Document Id,RHS Document Id,Match Percentage\n");
        documentMatches.forEach(documentMatch -> {
            csv.append(documentMatch.isUnmatched() ? "Unmatched" : "Matched").append(",")
                    .append(documentMatch.getLhsDocument().getId()).append(",")
                    .append(documentMatch.isUnmatched() ? "" : documentMatch.getRhsDocument().getId()).append(",")
                    .append(documentMatch.isUnmatched() ? "" : documentMatch.getSimilarityScore() * 100)
                    .append("\n");
        });
        return csv.toString();
    }

    @Data
    public static class DocumentMatch {
        private final Document lhsDocument;
        private final Document rhsDocument;
        private final float similarityScore;

        public static DocumentMatch unmatched(Document lhsDocument) {
            return new DocumentMatch(lhsDocument, null, -1f);
        }

        public boolean isUnmatched() {
            return rhsDocument == null;
        }
    }
}
