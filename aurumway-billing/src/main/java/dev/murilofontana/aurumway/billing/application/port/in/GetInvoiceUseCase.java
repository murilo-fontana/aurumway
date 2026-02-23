package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface GetInvoiceUseCase {

    GetInvoiceResult execute(String invoiceId);

    record GetInvoiceResult(
            String invoiceId, String customerId, String invoiceNumber, String currency,
            BigDecimal totalAmount, String status, BigDecimal refundedAmount,
            LocalDate issueDate, LocalDate dueDate,
            Instant createdAt, List<LineResult> lines
    ) {}

    record LineResult(String lineId, String description, int quantity,
                      BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {}
}
