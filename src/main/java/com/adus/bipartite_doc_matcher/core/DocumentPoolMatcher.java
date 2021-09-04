package com.adus.bipartite_doc_matcher.core;

import com.adus.bipartite_doc_matcher.input.DocumentPool;
import com.adus.bipartite_doc_matcher.output.MatchResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class DocumentPoolMatcher {
    private final DocumentPool documentPool;

    public MatchResult doMatch() {
        return new MatchResult(
                matchAndGetScores()
                        .stream()
                        .map(docSimilarity -> new MatchResult.DocumentMatch(
                                docSimilarity.getLhs().getDocument(), docSimilarity.getRhs().getDocument(),
                                docSimilarity.getSimilarityScore()))
                        .collect(Collectors.toList())
        );
    }

    private List<MatchableDocumentSimilarity> matchAndGetScores() {
        return doBipartiteMatching(
                computeSimilarityScores(documentPool.getLhsMatchableDocuments(), documentPool.getRhsMatchableDocuments())
        );
    }

    private List<MatchableDocumentSimilarity> doBipartiteMatching(List<MatchableDocumentSimilarity> similarityScores) {

        Map<String, MatchableDocument> probableUnmatchedLhsDocs = new HashMap<>();
        Set<String> matchedRhsDocs = new HashSet<>();
        Map<String, MatchableDocumentSimilarity> matchedLhsDocs = new HashMap<>();

        // Iterate over similarity scores in the decreasing order of similarities
        // to find high score matches for LHS documents
        similarityScores.sort(Comparator.comparing(MatchableDocumentSimilarity::getSimilarityScore).reversed());
        for (MatchableDocumentSimilarity docSim : similarityScores) {
            String lhsDocId = docSim.getLhs().getId();
            String rhsDocId = docSim.getRhs().getId();

            // A RHS-document can be matched with at most one LHS-document
            if (!matchedRhsDocs.contains(rhsDocId)) {
                if (!matchedLhsDocs.containsKey(lhsDocId)) {
                    matchedLhsDocs.put(lhsDocId, docSim);
                    matchedRhsDocs.add(rhsDocId);
                }
            } else {
                // Keep track of LHS-documents which haven't found a match(PROBABLY) in RHS-documents
                probableUnmatchedLhsDocs.put(lhsDocId, docSim.getLhs());
            }
        }

        // Decide which LHS-documents are not matched with any RHS-documents
        probableUnmatchedLhsDocs.keySet().removeAll(matchedLhsDocs.keySet());

        Stream<MatchableDocumentSimilarity> matchedSimilarities = matchedLhsDocs.values().stream();
        Stream<MatchableDocumentSimilarity> unmatchedSimilarities = probableUnmatchedLhsDocs
                .values()
                .stream()
                .map(MatchableDocumentSimilarity::unmatchedSimilarity);
        return Stream.concat(matchedSimilarities, unmatchedSimilarities)
                .collect(Collectors.toList());
    }


    private List<MatchableDocumentSimilarity> computeSimilarityScores(List<MatchableDocument> lhsDocuments, List<MatchableDocument> rhsDocuments) {
        List<Triple<MatchableDocument, SimilarityScoreValue, MatchableDocument>> rawSimilarityScores = new ArrayList<>();

        for (MatchableDocument lhsDoc : lhsDocuments) {
            for (MatchableDocument rhsDoc : rhsDocuments) {
                // calculate similarity between a LHS-document and a RHS-document
                SimilarityScoreValue similarityScoreValue = lhsDoc.compareTo(rhsDoc);
                rawSimilarityScores.add(Triple.of(lhsDoc, similarityScoreValue, rhsDoc));
            }
        }

        BigDecimal normalizationFactor = rawSimilarityScores.stream()
                .map(Triple::getMiddle)
                .map(SimilarityScoreValue::getNormDifference)
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ONE);

        return rawSimilarityScores
                .stream()
                .map(rawSim -> new MatchableDocumentSimilarity(rawSim.getLeft(), rawSim.getRight(),
                        rawSim.getMiddle().normalizeAndGet(normalizationFactor)))
                .collect(Collectors.toList());
    }

}
