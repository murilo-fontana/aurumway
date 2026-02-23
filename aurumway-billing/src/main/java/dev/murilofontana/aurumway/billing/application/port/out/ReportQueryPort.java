package dev.murilofontana.aurumway.billing.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ReportQueryPort {

    List<AccountBalanceRow> trialBalance(String currency);

    List<AgingRow> agingReport(String currency, LocalDate asOf);

    List<RevenueRow> revenueSummary(String currency, Instant from, Instant to);

    record AccountBalanceRow(String accountCode, BigDecimal totalDebit, BigDecimal totalCredit) {}

    record AgingRow(String invoiceId, String invoiceNumber, String customerId,
                    BigDecimal totalAmount, String currency, LocalDate dueDate, String status) {}

    record RevenueRow(String period, BigDecimal revenue, BigDecimal tax, long invoiceCount) {}
}
