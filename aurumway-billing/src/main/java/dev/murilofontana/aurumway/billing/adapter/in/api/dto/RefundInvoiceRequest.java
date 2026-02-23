package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;

public record RefundInvoiceRequest(BigDecimal amount, String reason) {}
