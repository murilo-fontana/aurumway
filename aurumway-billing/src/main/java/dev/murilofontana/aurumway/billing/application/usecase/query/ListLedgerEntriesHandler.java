package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.ListLedgerEntriesUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ListLedgerEntriesHandler implements ListLedgerEntriesUseCase {

    private final LedgerRepositoryPort repository;

    public ListLedgerEntriesHandler(LedgerRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntrySummary> execute(String entryType, Instant from, Instant to) {
        return repository.findWithFilters(entryType, from, to).stream()
                .map(e -> new LedgerEntrySummary(
                        e.id().value(), e.entryNumber(), e.entryType().name(),
                        e.referenceId(), e.currency(), e.description(), e.createdAt()
                ))
                .toList();
    }
}
