package dev.murilofontana.aurumway.contracts.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface GenerateInvoicesUseCase {

    GenerateInvoicesResult execute();

    record GenerateInvoicesResult(int contractsProcessed, int invoicesGenerated,
                                  List<GeneratedInvoice> invoices) {}

    record GeneratedInvoice(String contractId, String invoiceId, BigDecimal totalAmount, String currency) {}
}
