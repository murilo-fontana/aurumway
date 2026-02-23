package dev.murilofontana.aurumway.payments.adapter.in.api.dto;

public record CreatePaymentIntentResponse(
        String paymentId,
        String clientSecret,
        String status
) {}
