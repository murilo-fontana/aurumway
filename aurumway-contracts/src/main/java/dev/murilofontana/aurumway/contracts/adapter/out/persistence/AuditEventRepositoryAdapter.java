package dev.murilofontana.aurumway.contracts.adapter.out.persistence;

import dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity.AuditEventEntity;
import dev.murilofontana.aurumway.contracts.adapter.out.persistence.repository.JpaAuditEventRepository;
import dev.murilofontana.aurumway.contracts.application.port.out.AuditEventRepositoryPort;
import dev.murilofontana.aurumway.contracts.config.TenantContext;
import dev.murilofontana.aurumway.contracts.domain.model.AuditEvent;
import dev.murilofontana.aurumway.contracts.domain.valueobject.AuditAction;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class AuditEventRepositoryAdapter implements AuditEventRepositoryPort {

    private final JpaAuditEventRepository jpa;

    public AuditEventRepositoryAdapter(JpaAuditEventRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(AuditEvent event) {
        var entity = new AuditEventEntity(
                event.eventId(), event.tenantId(), event.actor(), event.action().name(),
                event.entityType(), event.entityId(), event.payload(), event.createdAt()
        );
        jpa.save(entity);
    }

    @Override
    public List<AuditEvent> findWithFilters(String action, String entityType, String entityId,
                                            String actor, Instant from, Instant to) {
        return jpa.findWithFilters(TenantContext.getCurrentTenant(), action, entityType, entityId, actor, from, to)
                .stream()
                .map(e -> AuditEvent.rehydrate(
                        e.getEventId(), e.getTenantId(), e.getActor(),
                        AuditAction.valueOf(e.getAction()), e.getEntityType(),
                        e.getEntityId(), e.getPayload(), e.getCreatedAt()))
                .toList();
    }
}
