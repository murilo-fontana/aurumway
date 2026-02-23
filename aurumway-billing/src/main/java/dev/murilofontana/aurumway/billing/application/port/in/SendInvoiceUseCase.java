package dev.murilofontana.aurumway.billing.application.port.in;

public interface SendInvoiceUseCase {

    SendInvoiceResult execute(String invoiceId);

    record SendInvoiceResult(String invoiceId, String status) {}
}
