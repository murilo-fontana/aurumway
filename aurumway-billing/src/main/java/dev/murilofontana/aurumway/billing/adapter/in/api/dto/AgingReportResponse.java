package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record AgingReportResponse(
        String currency, List<AgingBucketDto> buckets, BigDecimal totalOutstanding
) {
    public record AgingBucketDto(String label, int invoiceCount, BigDecimal amount) {}
}
