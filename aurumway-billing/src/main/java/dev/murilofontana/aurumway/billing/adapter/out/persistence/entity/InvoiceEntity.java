package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @Column(name = "invoice_id", nullable = false, updatable = false)
    private String invoiceId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal refundedAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceLineEntity> lines = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.billing.config.TenantContext.getCurrentTenant();
        }
    }

    protected InvoiceEntity() {}

    public InvoiceEntity(String invoiceId, String customerId, String invoiceNumber, String currency,
                         BigDecimal totalAmount, String status, LocalDate issueDate, LocalDate dueDate,
                         BigDecimal refundedAmount, Instant createdAt, List<InvoiceLineEntity> lines) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.invoiceNumber = invoiceNumber;
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.status = status;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.refundedAmount = refundedAmount;
        this.createdAt = createdAt;
        this.lines = lines;
        lines.forEach(line -> line.setInvoice(this));
    }

    public String getTenantId() { return tenantId; }
    public String getInvoiceId() { return invoiceId; }
    public String getCustomerId() { return customerId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCurrency() { return currency; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public Instant getCreatedAt() { return createdAt; }
    public List<InvoiceLineEntity> getLines() { return lines; }

    public BigDecimal getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(BigDecimal refundedAmount) { this.refundedAmount = refundedAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
