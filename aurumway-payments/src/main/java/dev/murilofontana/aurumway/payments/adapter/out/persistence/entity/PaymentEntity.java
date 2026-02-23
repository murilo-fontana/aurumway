package dev.murilofontana.aurumway.payments.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.math.BigDecimal;
import java.time.Instant;

@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @Column(name = "payment_id", nullable = false, updatable = false)
    private String paymentId;

    @Column(name = "external_reference", nullable = false)
    private String externalReference;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal refundedAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.payments.config.TenantContext.getCurrentTenant();
        }
    }

    protected PaymentEntity() {}

    public PaymentEntity(String paymentId, String externalReference, String provider,
                         BigDecimal amount, String currency, String status,
                         String stripePaymentIntentId, BigDecimal refundedAmount, Instant createdAt) {
        this.paymentId = paymentId;
        this.externalReference = externalReference;
        this.provider = provider;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.refundedAmount = refundedAmount;
        this.createdAt = createdAt;
    }

    public String getPaymentId() { return paymentId; }
    public String getExternalReference() { return externalReference; }
    public String getProvider() { return provider; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String id) { this.stripePaymentIntentId = id; }
    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal amount) { this.refundedAmount = amount; }
    public Instant getCreatedAt() { return createdAt; }
    public String getTenantId() { return tenantId; }
}
