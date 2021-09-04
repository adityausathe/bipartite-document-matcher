package com.adus.bipartite_doc_matcher.core;

import com.adus.bipartite_doc_matcher.common.IdAware;
import com.adus.bipartite_doc_matcher.input.Document;
import com.adus.bipartite_doc_matcher.input.MatchMode;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adus.bipartite_doc_matcher.common.Utils.square;

@Data
public class MatchableDocument implements IdAware {
    private final Document document;

    public MatchableDocument(Document document) {
        Objects.requireNonNull(document);
        this.document = document;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.document.getId();
    }

    public SimilarityScoreValue compareTo(MatchableDocument that) {
        BigDecimal hashSimilarity = compareVectors(HashComparableAttributesVector::new, that);
        BigDecimal normDifference = compareVectors(NormComparableAttributesVector::new, that);

        return new SimilarityScoreValue(hashSimilarity, normDifference);
    }

    private BigDecimal compareVectors(Function<MatchableDocument, ComparableAttributesVector> vectorCreator, MatchableDocument that) {
        return vectorCreator.apply(this).compareTo(vectorCreator.apply(that));
    }

    private static Stream<Pair<String, Object>> filterAndGetRawAttributeValues(MatchableDocument matchableDocument, MatchMode matchMode) {
        return matchableDocument.getDocument()
                .getAttributes()
                .entrySet()
                .stream()
                .map(e -> Pair.of(e.getKey(), e.getValue().getAsSimpleValue()))
                .filter(kv -> kv.getValue().getMatchMode().equals(matchMode))
                .map(kv -> Pair.of(kv.getKey(), kv.getValue().getAsSimpleValue().getValue()));
    }

    private interface ComparableAttributesVector {
        BigDecimal compareTo(ComparableAttributesVector otherVector);

    }

    private static class HashComparableAttributesVector implements ComparableAttributesVector {
        private final Map<String, Object> vector;

        HashComparableAttributesVector(MatchableDocument matchableDocument) {
            vector = filterAndGetRawAttributeValues(matchableDocument, MatchMode.HASH)
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        }

        /**
         * large-value = more similar
         */
        @Override
        public BigDecimal compareTo(ComparableAttributesVector otherVector) {
            HashComparableAttributesVector that = (HashComparableAttributesVector) otherVector;
            long commonAttributeCount = this.vector.keySet()
                    .stream()
                    .filter(that.vector::containsKey)
                    .count();
            long matchCount = this.vector
                    .keySet()
                    .stream()
                    .map(attrName -> Pair.of(this.vector.get(attrName), that.vector.get(attrName)))
                    .filter(valuePair -> Objects.equals(valuePair.getLeft(), valuePair.getRight()))
                    .count();
            return BigDecimal.valueOf((float) matchCount / commonAttributeCount);
        }
    }

    private static class NormComparableAttributesVector implements ComparableAttributesVector {
        private final Map<String, BigDecimal> vector;

        NormComparableAttributesVector(MatchableDocument matchableDocument) {
            vector = filterAndGetRawAttributeValues(matchableDocument, MatchMode.NORM)
                    .map(kv -> {
                        if (!(kv.getValue() instanceof Number)) {
                            throw new IllegalArgumentException("Norm match-type requires Number attribute-type.");
                        }
                        return Pair.of(kv.getKey(), new BigDecimal(kv.getValue().toString()));
                    })
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        }

        /**
         * large-value = less similar
         */
        @Override
        public BigDecimal compareTo(ComparableAttributesVector otherVector) {
            NormComparableAttributesVector that = (NormComparableAttributesVector) otherVector;
            return this.vector
                    .keySet()
                    .stream()
                    .map(attrName -> Pair.of(this.vector.get(attrName), that.vector.get(attrName)))
                    .map(valuePair -> square(valuePair.getLeft().subtract(valuePair.getRight())))
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);
        }
    }
}