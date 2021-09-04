package com.adus.bipartite_doc_matcher.core;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class SimilarityScoreValue {
    private static final int DIVISION_SCALE = 4;
    private final BigDecimal hashSimilarity;
    private final BigDecimal normDifference;

    public SimilarityScoreValue(BigDecimal hashSimilarity, BigDecimal normDifference) {
        this.hashSimilarity = hashSimilarity;
        this.normDifference = normDifference;
    }

    public float normalizeAndGet(BigDecimal normalizationFactor) {
        // collapse two distinct similarity scores into one
        // = (hashSimilarity + (1 - normDifference/normalizationFactor))/2
        return hashSimilarity.add(
                BigDecimal.ONE.subtract(normDifference
                        .divide(normalizationFactor, DIVISION_SCALE, RoundingMode.HALF_UP))
        ).divide(BigDecimal.valueOf(2), DIVISION_SCALE, RoundingMode.HALF_UP)
                .floatValue();
    }
}
