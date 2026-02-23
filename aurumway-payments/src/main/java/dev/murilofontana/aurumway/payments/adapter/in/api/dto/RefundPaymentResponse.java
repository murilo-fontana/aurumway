package dev.murilofontana.aurumway.payments.adapter.in.api.dto;

import java.math.BigDecimal;

public record RefundPaymentResponse(String paymentId, String status, BigDecimal refundedAmount,
                                    BigDecimal refundableBalance, String stripeRefundId) {}
