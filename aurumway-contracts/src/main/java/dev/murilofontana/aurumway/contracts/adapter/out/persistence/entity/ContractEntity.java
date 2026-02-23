package dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "contracts")
public class ContractEntity {

    @Id
    @Column(name = "contract_id", nullable = false, updatable = false)
    private String contractId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ContractItemEntity> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.contracts.config.TenantContext.getCurrentTenant();
        }
    }

    protected ContractEntity() {}

    public ContractEntity(String contractId, String customerId, String customerName, String currency,
                          String billingCycle, LocalDate startDate, LocalDate endDate,
                          String status, LocalDate nextBillingDate, Instant createdAt,
                          List<ContractItemEntity> items) {
        this.contractId = contractId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.currency = currency;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.nextBillingDate = nextBillingDate;
        this.createdAt = createdAt;
        this.items = items;
        items.forEach(i -> i.setContract(this));
    }

    public String getContractId() { return contractId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCurrency() { return currency; }
    public String getBillingCycle() { return billingCycle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public Instant getCreatedAt() { return createdAt; }
    public String getTenantId() { return tenantId; }
    public List<ContractItemEntity> getItems() { return items; }

    public void setStatus(String status) { this.status = status; }
    public void setNextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; }
}
