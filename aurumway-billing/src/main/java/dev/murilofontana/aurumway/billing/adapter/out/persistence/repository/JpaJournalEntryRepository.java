package dev.murilofontana.aurumway.billing.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.JournalEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaJournalEntryRepository extends JpaRepository<JournalEntryEntity, String> {

    Optional<JournalEntryEntity> findByEntryId(String entryId);

    List<JournalEntryEntity> findByReferenceId(String referenceId);

    @Query(value = "SELECT * FROM journal_entries e WHERE " +
           "e.tenant_id = :tenantId AND " +
           "(:entryType IS NULL OR e.entry_type = :entryType) AND " +
           "(CAST(:fromDate AS TIMESTAMPTZ) IS NULL OR e.created_at >= CAST(:fromDate AS TIMESTAMPTZ)) AND " +
           "(CAST(:toDate AS TIMESTAMPTZ) IS NULL OR e.created_at <= CAST(:toDate AS TIMESTAMPTZ)) " +
           "ORDER BY e.created_at DESC",
           nativeQuery = true)
    List<JournalEntryEntity> findWithFilters(String tenantId, String entryType, Instant fromDate, Instant toDate);

    @Query(value = "SELECT nextval('journal_entry_number_seq')", nativeQuery = true)
    long nextEntryNumber();
}
