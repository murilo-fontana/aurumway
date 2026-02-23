package dev.murilofontana.aurumway.contracts.adapter.in.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record GenerateInvoicesResponse(int contractsProcessed, int invoicesGenerated,
                                       List<GeneratedInvoiceDto> invoices) {

    public record GeneratedInvoiceDto(String contractId, String invoiceId,
                                      BigDecimal totalAmount, String currency) {}
}
