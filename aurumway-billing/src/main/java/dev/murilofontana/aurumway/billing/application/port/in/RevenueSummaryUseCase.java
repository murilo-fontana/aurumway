package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RevenueSummaryUseCase {

    RevenueSummaryResult execute(String currency, LocalDate from, LocalDate to);

    record RevenueSummaryResult(String currency, LocalDate from, LocalDate to,
                                List<PeriodRevenue> periods, BigDecimal totalRevenue, BigDecimal totalTax) {}

    record PeriodRevenue(String period, BigDecimal revenue, BigDecimal tax, int invoiceCount) {}
}
