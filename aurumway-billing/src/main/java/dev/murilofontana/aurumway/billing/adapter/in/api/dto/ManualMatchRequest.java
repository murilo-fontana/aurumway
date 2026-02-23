package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ManualMatchRequest(@NotBlank String invoiceId) {}
