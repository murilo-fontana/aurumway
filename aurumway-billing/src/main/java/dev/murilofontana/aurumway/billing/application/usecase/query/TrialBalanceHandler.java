package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.TrialBalanceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.ReportQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TrialBalanceHandler implements TrialBalanceUseCase {

    private final ReportQueryPort reportQuery;

    public TrialBalanceHandler(ReportQueryPort reportQuery) {
        this.reportQuery = reportQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public TrialBalanceResult execute(String currency) {
        var rows = reportQuery.trialBalance(currency);

        var accounts = rows.stream()
                .map(r -> new AccountBalance(
                        r.accountCode(),
                        r.totalDebit(),
                        r.totalCredit(),
                        r.totalDebit().subtract(r.totalCredit())))
                .toList();

        var totalDebit = accounts.stream().map(AccountBalance::debit).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCredit = accounts.stream().map(AccountBalance::credit).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TrialBalanceResult(currency != null ? currency : "ALL", accounts, totalDebit, totalCredit);
    }
}
