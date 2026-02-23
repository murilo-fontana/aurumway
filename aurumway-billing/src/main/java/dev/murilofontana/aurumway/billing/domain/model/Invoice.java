package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.common.money.Money;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Invoice {

    private final InvoiceId id;
    private final CustomerId customerId;
    private final String currency;
    private final List<InvoiceLine> lines;
    private InvoiceStatus status;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal refundedAmount;
    private final Instant createdAt;

    private Invoice(InvoiceId id, CustomerId customerId, String currency, List<InvoiceLine> lines,
                    InvoiceStatus status, String invoiceNumber, LocalDate issueDate,
                    LocalDate dueDate, BigDecimal refundedAmount, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.currency = Objects.requireNonNull(currency);
        this.lines = Objects.requireNonNull(lines);
        this.status = Objects.requireNonNull(status);
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.refundedAmount = refundedAmount;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Invoice createDraft(CustomerId customerId, String currency, List<InvoiceLine> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one line");
        }
        return new Invoice(InvoiceId.newId(), customerId, currency, List.copyOf(lines),
                InvoiceStatus.DRAFT, null, null, null, BigDecimal.ZERO, Instant.now());
    }

    public static Invoice rehydrate(InvoiceId id, CustomerId customerId, String currency, List<InvoiceLine> lines,
                                    InvoiceStatus status, String invoiceNumber, LocalDate issueDate,
                                    LocalDate dueDate, BigDecimal refundedAmount, Instant createdAt) {
        return new Invoice(id, customerId, currency, List.copyOf(lines), status, invoiceNumber,
                issueDate, dueDate, refundedAmount != null ? refundedAmount : BigDecimal.ZERO, createdAt);
    }

    public void issue(String generatedNumber, LocalDate dueDate) {
        assertStatus(InvoiceStatus.DRAFT);
        Objects.requireNonNull(generatedNumber, "invoiceNumber is required");
        Objects.requireNonNull(dueDate, "dueDate is required");
        this.invoiceNumber = generatedNumber;
        this.issueDate = LocalDate.now();
        this.dueDate = dueDate;
        this.status = InvoiceStatus.ISSUED;
    }

    public void send() {
        assertStatus(InvoiceStatus.ISSUED);
        this.status = InvoiceStatus.SENT;
    }

    public void markPaid() {
        if (this.status != InvoiceStatus.SENT && this.status != InvoiceStatus.OVERDUE) {
            throw new IllegalStateException("Cannot mark as paid from status: " + this.status);
        }
        this.status = InvoiceStatus.PAID;
    }

    public void markOverdue() {
        assertStatus(InvoiceStatus.SENT);
        this.status = InvoiceStatus.OVERDUE;
    }

    public void cancel() {
        if (this.status == InvoiceStatus.PAID || this.status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel from status: " + this.status);
        }
        this.status = InvoiceStatus.CANCELLED;
    }

    public void refund(BigDecimal amount) {
        if (this.status != InvoiceStatus.PAID && this.status != InvoiceStatus.PARTIALLY_REFUNDED) {
            throw new IllegalStateException("Cannot refund from status: " + this.status);
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        var total = totalAmount().amount();
        var newRefundedTotal = this.refundedAmount.add(amount);
        if (newRefundedTotal.compareTo(total) > 0) {
            throw new IllegalArgumentException(
                    "Refund amount %s exceeds refundable balance %s".formatted(amount, total.subtract(this.refundedAmount)));
        }
        this.refundedAmount = newRefundedTotal;
        this.status = newRefundedTotal.compareTo(total) == 0
                ? InvoiceStatus.REFUNDED
                : InvoiceStatus.PARTIALLY_REFUNDED;
    }

    public Money totalAmount() {
        var sum = lines.stream()
                .map(InvoiceLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.of(sum, currency);
    }

    public BigDecimal refundableBalance() {
        return totalAmount().amount().subtract(refundedAmount);
    }

    private void assertStatus(InvoiceStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("Expected status %s but was %s".formatted(expected, this.status));
        }
    }

    public InvoiceId id() { return id; }
    public CustomerId customerId() { return customerId; }
    public String currency() { return currency; }
    public List<InvoiceLine> lines() { return Collections.unmodifiableList(lines); }
    public InvoiceStatus status() { return status; }
    public String invoiceNumber() { return invoiceNumber; }
    public LocalDate issueDate() { return issueDate; }
    public LocalDate dueDate() { return dueDate; }
    public BigDecimal refundedAmount() { return refundedAmount; }
    public Instant createdAt() { return createdAt; }
}
