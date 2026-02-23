package dev.murilofontana.aurumway.payments.domain.valueobject;

public enum AuditAction {
    PAYMENT_CREATED,
    PAYMENT_INTENT_CREATED,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED
}
