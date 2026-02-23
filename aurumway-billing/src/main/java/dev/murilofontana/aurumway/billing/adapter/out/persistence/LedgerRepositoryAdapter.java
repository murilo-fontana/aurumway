package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper.JournalPersistenceMapper;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.repository.JpaJournalEntryRepository;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.config.TenantContext;
import dev.murilofontana.aurumway.billing.config.TenantFilterSupport;
import dev.murilofontana.aurumway.billing.domain.model.JournalEntry;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalEntryId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class LedgerRepositoryAdapter implements LedgerRepositoryPort {

    private final JpaJournalEntryRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public LedgerRepositoryAdapter(JpaJournalEntryRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public JournalEntry save(JournalEntry entry) {
        var entity = JournalPersistenceMapper.toEntity(entry);
        var saved = jpa.save(entity);
        return JournalPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<JournalEntry> findById(JournalEntryId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByEntryId(id.value()).map(JournalPersistenceMapper::toDomain);
    }

    @Override
    public List<JournalEntry> findByReferenceId(String referenceId) {
        tenantFilter.enableTenantFilter();
        return jpa.findByReferenceId(referenceId).stream()
                .map(JournalPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<JournalEntry> findWithFilters(String entryType, Instant from, Instant to) {
        tenantFilter.enableTenantFilter();
        return jpa.findWithFilters(TenantContext.getCurrentTenant(), entryType, from, to).stream()
                .map(JournalPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public String nextEntryNumber() {
        long seq = jpa.nextEntryNumber();
        return "JE-%06d".formatted(seq);
    }
}
