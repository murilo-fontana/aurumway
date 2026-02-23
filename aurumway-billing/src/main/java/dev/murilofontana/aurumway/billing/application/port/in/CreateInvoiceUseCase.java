package dev.murilofontana.aurumway.billing.application.port.in;

import dev.murilofontana.aurumway.billing.application.usecase.command.CreateInvoiceCommand;

public interface CreateInvoiceUseCase {

    CreateInvoiceResult execute(CreateInvoiceCommand command);

    record CreateInvoiceResult(String invoiceId, String status, String totalAmount, String currency) {}
}
