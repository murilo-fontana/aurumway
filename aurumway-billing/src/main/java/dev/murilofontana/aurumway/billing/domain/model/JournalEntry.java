package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.EntryType;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalEntryId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class JournalEntry {

    private final JournalEntryId id;
    private final String entryNumber;
    private final EntryType entryType;
    private final String referenceId;
    private final String currency;
    private final String description;
    private final List<JournalLine> lines;
    private final Instant createdAt;

    private JournalEntry(JournalEntryId id, String entryNumber, EntryType entryType,
                         String referenceId, String currency, String description,
                         List<JournalLine> lines, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.entryNumber = Objects.requireNonNull(entryNumber);
        this.entryType = Objects.requireNonNull(entryType);
        this.referenceId = Objects.requireNonNull(referenceId);
        this.currency = Objects.requireNonNull(currency);
        this.description = Objects.requireNonNull(description);
        this.lines = List.copyOf(Objects.requireNonNull(lines));
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static JournalEntry create(String entryNumber, EntryType entryType, String referenceId,
                                      String currency, String description, List<JournalLine> lines) {
        if (lines == null || lines.size() < 2) {
            throw new IllegalArgumentException("Journal entry must have at least 2 lines");
        }
        assertBalanced(lines);
        return new JournalEntry(JournalEntryId.newId(), entryNumber, entryType,
                referenceId, currency, description, lines, Instant.now());
    }

    public static JournalEntry rehydrate(JournalEntryId id, String entryNumber, EntryType entryType,
                                         String referenceId, String currency, String description,
                                         List<JournalLine> lines, Instant createdAt) {
        return new JournalEntry(id, entryNumber, entryType, referenceId, currency, description, lines, createdAt);
    }

    private static void assertBalanced(List<JournalLine> lines) {
        var totalDebit = lines.stream().map(JournalLine::debit).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCredit = lines.stream().map(JournalLine::credit).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    "Journal entry is not balanced: debit=%s credit=%s".formatted(totalDebit, totalCredit));
        }
    }

    public JournalEntryId id() { return id; }
    public String entryNumber() { return entryNumber; }
    public EntryType entryType() { return entryType; }
    public String referenceId() { return referenceId; }
    public String currency() { return currency; }
    public String description() { return description; }
    public List<JournalLine> lines() { return Collections.unmodifiableList(lines); }
    public Instant createdAt() { return createdAt; }
}
