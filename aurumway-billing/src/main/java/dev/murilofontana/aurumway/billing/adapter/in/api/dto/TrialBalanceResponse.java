package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record TrialBalanceResponse(
        String currency, List<AccountBalanceDto> accounts,
        BigDecimal totalDebit, BigDecimal totalCredit
) {
    public record AccountBalanceDto(String accountCode, BigDecimal debit, BigDecimal credit, BigDecimal balance) {}
}
