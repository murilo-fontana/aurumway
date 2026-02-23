package dev.murilofontana.aurumway.contracts.domain.model;

import dev.murilofontana.aurumway.contracts.domain.valueobject.AuditAction;

import java.time.Instant;
import java.util.UUID;

public class AuditEvent {

    private final String eventId;
    private final String tenantId;
    private final String actor;
    private final AuditAction action;
    private final String entityType;
    private final String entityId;
    private final String payload;
    private final Instant createdAt;

    private AuditEvent(String eventId, String tenantId, String actor, AuditAction action,
                       String entityType, String entityId, String payload, Instant createdAt) {
        this.eventId = eventId;
        this.tenantId = tenantId;
        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public static AuditEvent create(String tenantId, String actor, AuditAction action,
                                    String entityType, String entityId, String payload) {
        return new AuditEvent(
                UUID.randomUUID().toString(), tenantId, actor, action,
                entityType, entityId, payload, Instant.now()
        );
    }

    public static AuditEvent rehydrate(String eventId, String tenantId, String actor, AuditAction action,
                                       String entityType, String entityId, String payload, Instant createdAt) {
        return new AuditEvent(eventId, tenantId, actor, action, entityType, entityId, payload, createdAt);
    }

    public String eventId() { return eventId; }
    public String tenantId() { return tenantId; }
    public String actor() { return actor; }
    public AuditAction action() { return action; }
    public String entityType() { return entityType; }
    public String entityId() { return entityId; }
    public String payload() { return payload; }
    public Instant createdAt() { return createdAt; }
}
