package dev.murilofontana.aurumway.contracts.domain.model;

import dev.murilofontana.aurumway.contracts.domain.valueobject.BillingCycle;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Contract {

    private final ContractId id;
    private final String customerId;
    private final String customerName;
    private final String currency;
    private final BillingCycle billingCycle;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private ContractStatus status;
    private LocalDate nextBillingDate;
    private final List<ContractItem> items;
    private final Instant createdAt;

    private Contract(ContractId id, String customerId, String customerName, String currency,
                     BillingCycle billingCycle, LocalDate startDate, LocalDate endDate,
                     ContractStatus status, LocalDate nextBillingDate,
                     List<ContractItem> items, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.customerName = Objects.requireNonNull(customerName);
        this.currency = Objects.requireNonNull(currency);
        this.billingCycle = Objects.requireNonNull(billingCycle);
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = Objects.requireNonNull(endDate);
        this.status = Objects.requireNonNull(status);
        this.nextBillingDate = nextBillingDate;
        this.items = List.copyOf(Objects.requireNonNull(items));
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Contract createDraft(String customerId, String customerName, String currency,
                                       BillingCycle billingCycle, LocalDate startDate, LocalDate endDate,
                                       List<ContractItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Contract must have at least one item");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        return new Contract(ContractId.newId(), customerId, customerName, currency,
                billingCycle, startDate, endDate, ContractStatus.DRAFT, null, items, Instant.now());
    }

    public static Contract rehydrate(ContractId id, String customerId, String customerName, String currency,
                                     BillingCycle billingCycle, LocalDate startDate, LocalDate endDate,
                                     ContractStatus status, LocalDate nextBillingDate,
                                     List<ContractItem> items, Instant createdAt) {
        return new Contract(id, customerId, customerName, currency, billingCycle,
                startDate, endDate, status, nextBillingDate, items, createdAt);
    }

    public void activate() {
        if (status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Can only activate a DRAFT contract, current: " + status);
        }
        this.status = ContractStatus.ACTIVE;
        this.nextBillingDate = startDate;
    }

    public void suspend() {
        if (status != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Can only suspend an ACTIVE contract, current: " + status);
        }
        this.status = ContractStatus.SUSPENDED;
    }

    public void resume() {
        if (status != ContractStatus.SUSPENDED) {
            throw new IllegalStateException("Can only resume a SUSPENDED contract, current: " + status);
        }
        this.status = ContractStatus.ACTIVE;
    }

    public void terminate() {
        if (status != ContractStatus.ACTIVE && status != ContractStatus.SUSPENDED) {
            throw new IllegalStateException("Can only terminate ACTIVE or SUSPENDED contract, current: " + status);
        }
        this.status = ContractStatus.TERMINATED;
        this.nextBillingDate = null;
    }

    public void markExpired() {
        if (status != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Can only expire an ACTIVE contract, current: " + status);
        }
        this.status = ContractStatus.EXPIRED;
        this.nextBillingDate = null;
    }

    public boolean isDueFoBilling(LocalDate asOf) {
        return status == ContractStatus.ACTIVE
                && nextBillingDate != null
                && !nextBillingDate.isAfter(asOf)
                && !asOf.isAfter(endDate);
    }

    public void advanceBillingDate() {
        if (nextBillingDate == null) return;
        this.nextBillingDate = switch (billingCycle) {
            case MONTHLY -> nextBillingDate.plusMonths(1);
            case QUARTERLY -> nextBillingDate.plusMonths(3);
            case SEMI_ANNUAL -> nextBillingDate.plusMonths(6);
            case ANNUAL -> nextBillingDate.plusYears(1);
        };

        if (nextBillingDate.isAfter(endDate)) {
            this.nextBillingDate = null;
        }
    }

    public BigDecimal totalPerCycle() {
        return items.stream().map(ContractItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ContractId id() { return id; }
    public String customerId() { return customerId; }
    public String customerName() { return customerName; }
    public String currency() { return currency; }
    public BillingCycle billingCycle() { return billingCycle; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }
    public ContractStatus status() { return status; }
    public LocalDate nextBillingDate() { return nextBillingDate; }
    public List<ContractItem> items() { return Collections.unmodifiableList(items); }
    public Instant createdAt() { return createdAt; }
}
