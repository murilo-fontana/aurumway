package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;

import java.time.Instant;

@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity {

    @Id
    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.billing.config.TenantContext.getCurrentTenant();
        }
    }

    protected IdempotencyKeyEntity() {}

    public IdempotencyKeyEntity(String idempotencyKey, String endpoint, String resourceId, Instant createdAt) {
        this.idempotencyKey = idempotencyKey;
        this.endpoint = endpoint;
        this.resourceId = resourceId;
        this.createdAt = createdAt;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public String getEndpoint() { return endpoint; }
    public String getResourceId() { return resourceId; }
    public String getTenantId() { return tenantId; }
    public Instant getCreatedAt() { return createdAt; }
}
