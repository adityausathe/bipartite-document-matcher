package com.adus.bipartite_doc_matcher.common;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Utils {
    public static BigDecimal square(BigDecimal number) {
        return number.multiply(number);
    }
}
