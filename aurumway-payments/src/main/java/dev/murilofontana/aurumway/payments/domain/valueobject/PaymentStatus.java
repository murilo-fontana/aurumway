package dev.murilofontana.aurumway.payments.domain.valueobject;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCEEDED,
    PARTIALLY_REFUNDED,
    REFUNDED,
    FAILED,
    CANCELLED
}
