package dev.murilofontana.aurumway.payments.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface JpaAuditEventRepository extends JpaRepository<AuditEventEntity, String> {

    @Query(value = "SELECT * FROM audit_events a WHERE a.tenant_id = :tenantId " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:entityType IS NULL OR a.entity_type = :entityType) " +
           "AND (:entityId IS NULL OR a.entity_id = :entityId) " +
           "AND (:actor IS NULL OR a.actor = :actor) " +
           "AND (CAST(:fromDate AS TIMESTAMPTZ) IS NULL OR a.created_at >= CAST(:fromDate AS TIMESTAMPTZ)) " +
           "AND (CAST(:toDate AS TIMESTAMPTZ) IS NULL OR a.created_at <= CAST(:toDate AS TIMESTAMPTZ)) " +
           "ORDER BY a.created_at DESC LIMIT 100",
           nativeQuery = true)
    List<AuditEventEntity> findWithFilters(String tenantId, String action, String entityType,
                                           String entityId, String actor, Instant fromDate, Instant toDate);
}
