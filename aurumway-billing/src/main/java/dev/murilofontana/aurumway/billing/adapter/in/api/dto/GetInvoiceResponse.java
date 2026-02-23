package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record GetInvoiceResponse(
        String invoiceId, String customerId, String invoiceNumber, String currency,
        BigDecimal totalAmount, String status, BigDecimal refundedAmount,
        LocalDate issueDate, LocalDate dueDate,
        Instant createdAt, List<LineResponse> lines
) {
    public record LineResponse(String lineId, String description, int quantity,
                               BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {}
}
