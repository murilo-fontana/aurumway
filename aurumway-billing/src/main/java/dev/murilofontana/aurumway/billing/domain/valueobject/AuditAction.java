package dev.murilofontana.aurumway.billing.domain.valueobject;

public enum AuditAction {
    CUSTOMER_CREATED,
    INVOICE_CREATED,
    INVOICE_ISSUED,
    INVOICE_SENT,
    INVOICE_PAID,
    INVOICE_CANCELLED,
    INVOICE_REFUNDED,
    JOURNAL_ENTRY_CREATED,
    STATEMENT_IMPORTED,
    STATEMENT_RECONCILED
}
