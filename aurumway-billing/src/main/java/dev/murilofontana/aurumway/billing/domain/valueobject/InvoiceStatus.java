package dev.murilofontana.aurumway.billing.domain.valueobject;

public enum InvoiceStatus {
    DRAFT,
    ISSUED,
    SENT,
    PAID,
    PARTIALLY_REFUNDED,
    REFUNDED,
    OVERDUE,
    CANCELLED
}
