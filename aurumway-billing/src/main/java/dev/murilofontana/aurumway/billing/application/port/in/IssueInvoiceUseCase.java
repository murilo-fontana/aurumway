package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.LocalDate;

public interface IssueInvoiceUseCase {

    IssueInvoiceResult execute(String invoiceId, LocalDate dueDate);

    record IssueInvoiceResult(String invoiceId, String invoiceNumber, String status) {}
}
