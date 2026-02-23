package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IssueInvoiceRequest(@NotNull LocalDate dueDate) {}
