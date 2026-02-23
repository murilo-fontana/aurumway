package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.time.Instant;

@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(name = "customer_id", nullable = false, updatable = false)
    private String customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.billing.config.TenantContext.getCurrentTenant();
        }
    }

    protected CustomerEntity() {}

    public CustomerEntity(String customerId, String name, String email, String taxId, Instant createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.taxId = taxId;
        this.createdAt = createdAt;
    }

    public String getTenantId() { return tenantId; }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getTaxId() { return taxId; }
    public Instant getCreatedAt() { return createdAt; }
}
