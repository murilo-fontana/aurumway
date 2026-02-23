package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface AgingReportUseCase {

    AgingReportResult execute(String currency);

    record AgingReportResult(String currency, List<AgingBucket> buckets, BigDecimal totalOutstanding) {}

    record AgingBucket(String label, int invoiceCount, BigDecimal amount) {}
}
