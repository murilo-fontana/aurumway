package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;

public record RefundInvoiceResponse(String invoiceId, String status, BigDecimal refundedAmount,
                                    BigDecimal refundableBalance, String ledgerEntryId) {}
