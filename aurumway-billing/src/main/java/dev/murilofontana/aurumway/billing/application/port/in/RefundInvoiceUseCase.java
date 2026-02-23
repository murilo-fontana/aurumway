package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;

public interface RefundInvoiceUseCase {

    RefundInvoiceResult execute(String invoiceId, BigDecimal amount, String reason);

    record RefundInvoiceResult(String invoiceId, String status, BigDecimal refundedAmount,
                               BigDecimal refundableBalance, String ledgerEntryId) {}
}
