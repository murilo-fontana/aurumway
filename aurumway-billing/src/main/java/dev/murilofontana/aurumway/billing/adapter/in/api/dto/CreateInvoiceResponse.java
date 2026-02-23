package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

public record CreateInvoiceResponse(String invoiceId, String status, String totalAmount, String currency) {}
