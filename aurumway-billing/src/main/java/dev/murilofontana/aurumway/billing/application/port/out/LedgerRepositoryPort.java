package dev.murilofontana.aurumway.billing.application.port.out;

import dev.murilofontana.aurumway.billing.domain.model.JournalEntry;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalEntryId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LedgerRepositoryPort {

    JournalEntry save(JournalEntry entry);

    Optional<JournalEntry> findById(JournalEntryId id);

    List<JournalEntry> findByReferenceId(String referenceId);

    List<JournalEntry> findWithFilters(String entryType, Instant from, Instant to);

    String nextEntryNumber();
}
