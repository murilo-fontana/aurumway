package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.util.List;

public record ReconcileResponse(String statementId, String status, int matchedCount,
                                int unmatchedCount, List<MatchDetailDto> matches) {

    public record MatchDetailDto(String transactionId, String invoiceId, String invoiceNumber) {}
}
