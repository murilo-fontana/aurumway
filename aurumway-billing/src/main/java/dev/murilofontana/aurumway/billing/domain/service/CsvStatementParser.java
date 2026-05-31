package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.model.BankTransaction;
import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CSV bank statements.
 * Expected format: date,amount,currency,type,description,reference
 * First row is the header and is skipped.
 */
public final class CsvStatementParser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private CsvStatementParser() {}

    public static List<BankTransaction> parse(InputStream input) {
        var transactions = new ArrayList<BankTransaction>();
        try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            var header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isBlank()) continue;
                transactions.add(parseLine(line, lineNum));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read CSV: " + e.getMessage());
        }

        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("CSV file has no data rows");
        }
        return transactions;
    }

    private static BankTransaction parseLine(String line, int lineNum) {
        var parts = line.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Line %d: expected at least 4 columns, got %d".formatted(lineNum, parts.length));
        }

        var date = parseDate(parts[0].trim(), lineNum);
        var amount = parseAmount(parts[1].trim(), lineNum);
        var currency = parts[2].trim().toUpperCase();
        var type = parseType(parts[3].trim().toUpperCase(), lineNum);
        var description = parts.length > 4 ? parts[4].trim() : null;
        var reference = parts.length > 5 ? parts[5].trim() : null;

        return BankTransaction.create(date, amount, currency, description, reference, type);
    }

    private static LocalDate parseDate(String value, int lineNum) {
        try {
            return LocalDate.parse(value, DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Line %d: invalid date '%s', expected ISO yyyy-MM-dd".formatted(lineNum, value));
        }
    }

    private static BigDecimal parseAmount(String value, int lineNum) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Line %d: invalid amount '%s'".formatted(lineNum, value));
        }
    }

    private static TransactionType parseType(String value, int lineNum) {
        try {
            return TransactionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Line %d: invalid transaction type '%s'".formatted(lineNum, value));
        }
    }
}
