package dev.murilofontana.aurumway.payments.domain.model;

import dev.murilofontana.aurumway.payments.common.money.Money;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class Payment {

    private final PaymentId id;
    private final String externalReference;
    private final String provider;
    private final Money money;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private BigDecimal refundedAmount;
    private final Instant createdAt;

    private Payment(PaymentId id, String externalReference, String provider,
                    Money money, PaymentStatus status, String stripePaymentIntentId,
                    BigDecimal refundedAmount, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.externalReference = Objects.requireNonNull(externalReference, "externalReference is required");
        this.provider = Objects.requireNonNull(provider, "provider is required");
        this.money = Objects.requireNonNull(money, "money is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.refundedAmount = refundedAmount;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public static Payment createPending(String externalReference, String provider, Money money) {
        return new Payment(PaymentId.newId(), externalReference, provider, money,
                PaymentStatus.PENDING, null, BigDecimal.ZERO, Instant.now());
    }

    public static Payment rehydrate(PaymentId id, String externalReference, String provider,
                                    Money money, PaymentStatus status, String stripePaymentIntentId,
                                    BigDecimal refundedAmount, Instant createdAt) {
        return new Payment(id, externalReference, provider, money, status, stripePaymentIntentId,
                refundedAmount != null ? refundedAmount : BigDecimal.ZERO, createdAt);
    }

    public void markProcessing(String stripePaymentIntentId) {
        assertStatus(PaymentStatus.PENDING);
        this.status = PaymentStatus.PROCESSING;
        this.stripePaymentIntentId = Objects.requireNonNull(stripePaymentIntentId);
    }

    public void markSucceeded() {
        assertStatus(PaymentStatus.PROCESSING);
        this.status = PaymentStatus.SUCCEEDED;
    }

    public void markFailed() {
        assertStatus(PaymentStatus.PROCESSING);
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        assertStatus(PaymentStatus.PENDING);
        this.status = PaymentStatus.CANCELLED;
    }

    public void refund(BigDecimal amount) {
        if (this.status != PaymentStatus.SUCCEEDED && this.status != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new IllegalStateException("Cannot refund from status: " + this.status);
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        var total = this.money.amount();
        var newRefundedTotal = this.refundedAmount.add(amount);
        if (newRefundedTotal.compareTo(total) > 0) {
            throw new IllegalArgumentException(
                    "Refund amount %s exceeds refundable balance %s".formatted(amount, total.subtract(this.refundedAmount)));
        }
        this.refundedAmount = newRefundedTotal;
        this.status = newRefundedTotal.compareTo(total) == 0
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED;
    }

    public BigDecimal refundableBalance() {
        return money.amount().subtract(refundedAmount);
    }

    private void assertStatus(PaymentStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Expected status %s but was %s".formatted(expected, this.status));
        }
    }

    public PaymentId id() { return id; }
    public String externalReference() { return externalReference; }
    public String provider() { return provider; }
    public Money money() { return money; }
    public PaymentStatus status() { return status; }
    public String stripePaymentIntentId() { return stripePaymentIntentId; }
    public BigDecimal refundedAmount() { return refundedAmount; }
    public Instant createdAt() { return createdAt; }
}
