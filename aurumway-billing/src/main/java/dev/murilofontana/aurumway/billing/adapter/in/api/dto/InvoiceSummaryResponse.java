package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record InvoiceSummaryResponse(
        String invoiceId, String customerId, String invoiceNumber, String currency,
        BigDecimal totalAmount, String status, LocalDate dueDate, Instant createdAt
) {}
