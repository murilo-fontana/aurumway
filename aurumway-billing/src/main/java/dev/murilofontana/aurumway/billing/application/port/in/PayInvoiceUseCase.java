package dev.murilofontana.aurumway.billing.application.port.in;

public interface PayInvoiceUseCase {

    PayInvoiceResult execute(String invoiceId);

    record PayInvoiceResult(String invoiceId, String status) {}
}
