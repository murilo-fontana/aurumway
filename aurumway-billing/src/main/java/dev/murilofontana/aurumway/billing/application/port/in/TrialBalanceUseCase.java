package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface TrialBalanceUseCase {

    TrialBalanceResult execute(String currency);

    record TrialBalanceResult(String currency, List<AccountBalance> accounts,
                              BigDecimal totalDebit, BigDecimal totalCredit) {}

    record AccountBalance(String accountCode, BigDecimal debit, BigDecimal credit, BigDecimal balance) {}
}
