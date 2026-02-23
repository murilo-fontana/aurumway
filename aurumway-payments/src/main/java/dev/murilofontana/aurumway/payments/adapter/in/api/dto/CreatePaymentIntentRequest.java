package dev.murilofontana.aurumway.payments.adapter.in.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentIntentRequest(
        @NotBlank String externalReference,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String currency
) {}
