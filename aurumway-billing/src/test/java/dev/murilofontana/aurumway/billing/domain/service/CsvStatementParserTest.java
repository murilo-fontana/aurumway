package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvStatementParserTest {

    private static ByteArrayInputStream csv(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void parsesValidCsv() {
        var content = """
                date,amount,currency,type,description,reference
                2026-01-15,100.00,USD,CREDIT,Payment received,INV-001
                2026-01-16,50.50,usd,debit,Bank fee,
                """;

        var transactions = CsvStatementParser.parse(csv(content));

        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).amount()).isEqualByComparingTo("100.00");
        assertThat(transactions.get(0).currency()).isEqualTo("USD");
        assertThat(transactions.get(0).type()).isEqualTo(TransactionType.CREDIT);
        assertThat(transactions.get(0).reference()).isEqualTo("INV-001");
        assertThat(transactions.get(1).type()).isEqualTo(TransactionType.DEBIT);
    }

    @Test
    void skipsBlankLines() {
        var content = """
                date,amount,currency,type,description,reference
                2026-01-15,100.00,USD,CREDIT,Payment,INV-001

                2026-01-16,200.00,USD,CREDIT,Payment,INV-002
                """;

        var transactions = CsvStatementParser.parse(csv(content));

        assertThat(transactions).hasSize(2);
    }

    @Test
    void rejectsEmptyFile() {
        assertThatThrownBy(() -> CsvStatementParser.parse(csv("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void rejectsHeaderOnlyFile() {
        assertThatThrownBy(() -> CsvStatementParser.parse(csv("date,amount,currency,type\n")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no data rows");
    }

    @Test
    void rejectsLineWithTooFewColumns() {
        var content = """
                date,amount,currency,type
                2026-01-15,100.00,USD
                """;

        assertThatThrownBy(() -> CsvStatementParser.parse(csv(content)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expected at least 4 columns");
    }
}
