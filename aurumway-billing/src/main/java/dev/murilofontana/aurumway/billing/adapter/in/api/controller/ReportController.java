package dev.murilofontana.aurumway.billing.adapter.in.api.controller;

import dev.murilofontana.aurumway.billing.adapter.in.api.dto.AgingReportResponse;
import dev.murilofontana.aurumway.billing.adapter.in.api.dto.RevenueSummaryResponse;
import dev.murilofontana.aurumway.billing.adapter.in.api.dto.TrialBalanceResponse;
import dev.murilofontana.aurumway.billing.application.port.in.AgingReportUseCase;
import dev.murilofontana.aurumway.billing.application.port.in.RevenueSummaryUseCase;
import dev.murilofontana.aurumway.billing.application.port.in.TrialBalanceUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final TrialBalanceUseCase trialBalanceUseCase;
    private final AgingReportUseCase agingReportUseCase;
    private final RevenueSummaryUseCase revenueSummaryUseCase;

    public ReportController(TrialBalanceUseCase trialBalanceUseCase,
                            AgingReportUseCase agingReportUseCase,
                            RevenueSummaryUseCase revenueSummaryUseCase) {
        this.trialBalanceUseCase = trialBalanceUseCase;
        this.agingReportUseCase = agingReportUseCase;
        this.revenueSummaryUseCase = revenueSummaryUseCase;
    }

    @GetMapping("/trial-balance")
    public TrialBalanceResponse trialBalance(@RequestParam(required = false) String currency) {
        var result = trialBalanceUseCase.execute(currency);
        var accounts = result.accounts().stream()
                .map(a -> new TrialBalanceResponse.AccountBalanceDto(
                        a.accountCode(), a.debit(), a.credit(), a.balance()))
                .toList();
        return new TrialBalanceResponse(result.currency(), accounts, result.totalDebit(), result.totalCredit());
    }

    @GetMapping("/aging")
    public AgingReportResponse aging(@RequestParam(required = false) String currency) {
        var result = agingReportUseCase.execute(currency);
        var buckets = result.buckets().stream()
                .map(b -> new AgingReportResponse.AgingBucketDto(b.label(), b.invoiceCount(), b.amount()))
                .toList();
        return new AgingReportResponse(result.currency(), buckets, result.totalOutstanding());
    }

    @GetMapping("/revenue")
    public RevenueSummaryResponse revenue(
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var result = revenueSummaryUseCase.execute(currency, from, to);
        var periods = result.periods().stream()
                .map(p -> new RevenueSummaryResponse.PeriodRevenueDto(
                        p.period(), p.revenue(), p.tax(), p.invoiceCount()))
                .toList();
        return new RevenueSummaryResponse(result.currency(), result.from(), result.to(),
                periods, result.totalRevenue(), result.totalTax());
    }
}
