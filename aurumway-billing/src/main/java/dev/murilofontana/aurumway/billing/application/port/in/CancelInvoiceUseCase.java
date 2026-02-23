package dev.murilofontana.aurumway.billing.application.port.in;

public interface CancelInvoiceUseCase {

    CancelInvoiceResult execute(String invoiceId);

    record CancelInvoiceResult(String invoiceId, String status) {}
}
