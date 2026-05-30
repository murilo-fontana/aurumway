package dev.murilofontana.aurumway.payments.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Transactional outbox row. Written in the same transaction as the domain change
 * and relayed to the broker by a scheduled publisher (at-least-once delivery).
 * Intentionally not tenant-filtered so the relay sees every tenant's pending rows.
 */
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "routing_key", nullable = false)
    private String routingKey;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    protected OutboxEventEntity() {}

    public OutboxEventEntity(String id, String aggregateType, String aggregateId, String eventType,
                             String routingKey, String payload, String tenantId) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.routingKey = routingKey;
        this.payload = payload;
        this.tenantId = tenantId;
        this.status = "PENDING";
        this.attempts = 0;
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = "SENT";
        this.attempts += 1;
        this.lastAttemptAt = Instant.now();
    }

    public void recordFailure() {
        this.attempts += 1;
        this.lastAttemptAt = Instant.now();
    }

    public String getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getRoutingKey() { return routingKey; }
    public String getPayload() { return payload; }
    public String getTenantId() { return tenantId; }
    public String getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastAttemptAt() { return lastAttemptAt; }
}
