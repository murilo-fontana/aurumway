package dev.murilofontana.aurumway.billing.application.port.in;

import java.util.List;

public interface AutoReconcileUseCase {

    ReconcileResult execute(String statementId);

    record ReconcileResult(String statementId, String status, int matchedCount,
                           int unmatchedCount, List<MatchDetail> matches) {}

    record MatchDetail(String transactionId, String invoiceId, String invoiceNumber) {}
}
