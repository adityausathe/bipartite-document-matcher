package com.adus.bipartite_doc_matcher;

import com.adus.bipartite_doc_matcher.core.DocumentPoolMatcher;
import com.adus.bipartite_doc_matcher.input.*;
import com.adus.bipartite_doc_matcher.output.MatchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MatcherTest {

    @Test
    void testMatching() {
        DocumentPool documentPool = new DocumentPool(readLhsDocuments(), readRhsDocuments());
        MatchResult matchResult = new DocumentPoolMatcher(documentPool).doMatch();

        // Alphabet-letter ←→ Alphabet-index match is expected
        assertEquals("Matched/Unmatched,LHS Document Id,RHS Document Id,Match Percentage\n" +
                        "Matched,2,B,99.93\n" +
                        "Matched,1,A,87.49\n" +
                        "Matched,3,C,87.29\n" +
                        "Matched,5,E,75.0\n" +
                        "Matched,6,F,75.0\n" +
                        "Matched,4,D,74.86\n",
                matchResult.exportAsCsv());

        Function<MatchResult.DocumentMatch, String> printMatchInfo = match ->
                String.format("LHS(%s){%s} ←→ RHS(%s){%s}",
                        match.getLhsDocument().getId(), match.getLhsDocument().getAsSimpleValue().getValue(),
                        match.getRhsDocument().getId(), match.getRhsDocument().getAsSimpleValue().getValue());

        Iterator<MatchResult.DocumentMatch> documentMatches = matchResult.getDocumentMatches().iterator();
        assertEquals(
                "LHS(2){color[white] | category[value] | code[rgba[0 | 0 | 0 | 1] | hex[#FFF]] | intensity[39.13] | opacity[12.3] | gradient[1.9]}" +
                        " ←→ " +
                        "RHS(B){color[white] | category[value] | code[rgba[0 | 0 | 0 | 1] | hex[#FFF]] | intensity[36.5] | opacity[14.5] | gradient[2.5]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertEquals(
                "LHS(1){color[black] | category[hue] | type[primary] | code[rgba[255 | 255 | 255 | 1] | hex[#000]] | intensity[10.88] | opacity[12.3] | gradient[-1.2]}" +
                        " ←→ " +
                        "RHS(A){color[black] | category[hue] | type[primary] | code[rgba[255 | 255 | 255 | 1] | hex[#00D0]] | intensity[9.61] | opacity[12.3] | gradient[-1.9]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertEquals(
                "LHS(3){color[red] | category[hue] | type[primary] | code[rgba[255 | 0 | 0 | 1] | hex[#FF0]] | intensity[22.11] | opacity[8.3] | gradient[-1.2]}" +
                        " ←→ " +
                        "RHS(C){color[red] | category[hue] | type[primary] | code[rgba[255 | 0 | 0 | 1] | hex[#DF0C]] | intensity[16.31] | opacity[7.7] | gradient[-0.8]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertEquals(
                "LHS(5){color[yellow] | category[hue] | type[primary] | code[rgba[255 | 255 | 0 | 1] | hex[#FF0]] | intensity[30.8] | opacity[2.15] | gradient[1.0]}" +
                        " ←→ " +
                        "RHS(E){color[yellow#2] | category[hue] | type[primary] | code[rgba[255 | 250 | 10 | 123] | hex[#FF0]] | intensity[30.8] | opacity[2.15] | gradient[1.0]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertEquals(
                "LHS(6){color[green] | category[hue] | type[secondary] | code[rgba[0 | 255 | 0 | 1] | hex[#0F0]] | intensity[50.6] | opacity[-3.98] | gradient[0.63]}" +
                        " ←→ " +
                        "RHS(F){color[green] | category[hue#2] | type[secondary] | code[rgba[0 | 255 | 10 | 1] | hex[#0FA]] | intensity[49.8] | opacity[-3.98] | gradient[0.63]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertEquals(
                "LHS(4){color[blue] | category[hue] | type[primary] | code[rgba[0 | 0 | 255 | 1] | hex[#00F]] | intensity[95.23] | opacity[6] | gradient[0.99]}" +
                        " ←→ " +
                        "RHS(D){color[blue] | category[hue] | type[primary#2] | code[rgba[23 | 0 | 255 | 1] | hex[#00F]] | intensity[99.52] | opacity[7.9] | gradient[1.91]}",
                printMatchInfo.apply(documentMatches.next())
        );
        assertFalse(documentMatches.hasNext());
    }

    private List<Document> readLhsDocuments() {
        return readDocuments("test_documents_lhs.json");
    }

    private List<Document> readRhsDocuments() {
        return readDocuments("test_documents_rhs.json");
    }

    private List<Document> readDocuments(String fileName) {
        return readJsonDocsFromFile(fileName)
                .stream()
                .map(this::toDocumentValue)
                .collect(Collectors.toList());
    }

    private Document toDocumentValue(Map<String, Object> jsonDocument) {
        Document document = new Document(Optional.ofNullable(jsonDocument.get("id")).map(Objects::toString).orElse(null));
        jsonDocument.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("id"))
                .forEach(nameValue -> document.addAttribute(nameValue.getKey(), callAppropriateConverter(nameValue.getValue())));
        return document;
    }

    private ArrayValue toArrayValue(Collection<Object> arrayLike) {
        AttributeValue[] elements = arrayLike.stream()
                .map(this::callAppropriateConverter)
                .toArray(AttributeValue[]::new);
        return new ArrayValue(elements.length > 0 ? ValueType.of(elements[0]) : ValueType.STRING, elements);
    }

    private SimpleValue toSimpleValue(Object value) {
        MatchMode matchMode = value instanceof Number ? MatchMode.NORM : MatchMode.HASH;
        return new SimpleValue(ValueType.of(value), matchMode, value);
    }

    private AttributeValue callAppropriateConverter(Object o) {
        if (o instanceof Map) {
            return toDocumentValue((Map<String, Object>) o);
        }
        if (o instanceof Collection) {
            return toArrayValue((Collection<Object>) o);
        }
        return toSimpleValue(o);
    }

    @SneakyThrows
    private List<Map<String, Object>> readJsonDocsFromFile(String fileName) {
        URL fileUrl = getClass().getClassLoader().getResource(fileName);
        List<Map<String, Object>> jsonDocuments = new ObjectMapper().readValue(fileUrl, List.class);
        jsonDocuments.stream()
                .forEach(jsonDocument -> {
                    System.out.println(jsonDocument);
                });
        return jsonDocuments;
    }
}