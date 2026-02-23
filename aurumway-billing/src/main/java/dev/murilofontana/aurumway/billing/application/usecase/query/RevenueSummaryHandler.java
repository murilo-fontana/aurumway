package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.RevenueSummaryUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.ReportQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class RevenueSummaryHandler implements RevenueSummaryUseCase {

    private final ReportQueryPort reportQuery;

    public RevenueSummaryHandler(ReportQueryPort reportQuery) {
        this.reportQuery = reportQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueSummaryResult execute(String currency, LocalDate from, LocalDate to) {
        var fromInstant = from != null ? from.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        var toInstant = to != null ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        var rows = reportQuery.revenueSummary(currency, fromInstant, toInstant);

        var periods = rows.stream()
                .map(r -> new PeriodRevenue(r.period(), r.revenue(), r.tax(), (int) r.invoiceCount()))
                .toList();

        var totalRevenue = periods.stream().map(PeriodRevenue::revenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalTax = periods.stream().map(PeriodRevenue::tax).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueSummaryResult(
                currency != null ? currency : "ALL", from, to, periods, totalRevenue, totalTax);
    }
}
