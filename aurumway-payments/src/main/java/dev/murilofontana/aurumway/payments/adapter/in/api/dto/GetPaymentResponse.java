package dev.murilofontana.aurumway.payments.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record GetPaymentResponse(
        String paymentId,
        String externalReference,
        String provider,
        BigDecimal amount,
        String currency,
        String status,
        BigDecimal refundedAmount,
        Instant createdAt
) {}
