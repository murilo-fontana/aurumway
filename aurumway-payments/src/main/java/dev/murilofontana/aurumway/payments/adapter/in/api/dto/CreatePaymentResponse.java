package dev.murilofontana.aurumway.payments.adapter.in.api.dto;

public record CreatePaymentResponse(
    String paymentId,
    String status
) {}