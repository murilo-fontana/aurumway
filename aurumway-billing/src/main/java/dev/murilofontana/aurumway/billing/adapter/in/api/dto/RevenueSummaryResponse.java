package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueSummaryResponse(
        String currency, LocalDate from, LocalDate to,
        List<PeriodRevenueDto> periods, BigDecimal totalRevenue, BigDecimal totalTax
) {
    public record PeriodRevenueDto(String period, BigDecimal revenue, BigDecimal tax, int invoiceCount) {}
}
