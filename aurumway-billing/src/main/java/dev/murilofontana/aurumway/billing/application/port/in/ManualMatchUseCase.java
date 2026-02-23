package dev.murilofontana.aurumway.billing.application.port.in;

public interface ManualMatchUseCase {

    ManualMatchResult execute(String statementId, String transactionId, String invoiceId);

    record ManualMatchResult(String transactionId, String invoiceId, String status) {}
}
