package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.application.port.out.ReportQueryPort;
import dev.murilofontana.aurumway.billing.config.TenantContext;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
public class ReportQueryAdapter implements ReportQueryPort {

    private final EntityManager em;

    public ReportQueryAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AccountBalanceRow> trialBalance(String currency) {
        var sql = """
                SELECT l.account_code,
                       COALESCE(SUM(l.debit), 0)  AS total_debit,
                       COALESCE(SUM(l.credit), 0) AS total_credit
                FROM journal_lines l
                JOIN journal_entries e ON e.entry_id = l.entry_id
                WHERE (CAST(:currency AS VARCHAR) IS NULL OR e.currency = CAST(:currency AS VARCHAR))
                  AND e.tenant_id = :tenantId
                GROUP BY l.account_code
                ORDER BY l.account_code
                """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("currency", currency)
                .setParameter("tenantId", TenantContext.getCurrentTenant())
                .getResultList();

        return rows.stream()
                .map(r -> new AccountBalanceRow(
                        (String) r[0],
                        toBigDecimal(r[1]),
                        toBigDecimal(r[2])
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AgingRow> agingReport(String currency, LocalDate asOf) {
        var sql = """
                SELECT i.invoice_id, i.invoice_number, i.customer_id,
                       i.total_amount, i.currency, i.due_date, i.status
                FROM invoices i
                WHERE i.status IN ('ISSUED', 'SENT', 'OVERDUE')
                  AND i.tenant_id = :tenantId
                  AND (CAST(:currency AS VARCHAR) IS NULL OR i.currency = CAST(:currency AS VARCHAR))
                ORDER BY i.due_date ASC
                """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("currency", currency)
                .setParameter("tenantId", TenantContext.getCurrentTenant())
                .getResultList();

        return rows.stream()
                .map(r -> new AgingRow(
                        (String) r[0], (String) r[1], (String) r[2],
                        toBigDecimal(r[3]), (String) r[4],
                        r[5] != null ? ((Date) r[5]).toLocalDate() : null,
                        (String) r[6]
                ))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RevenueRow> revenueSummary(String currency, Instant from, Instant to) {
        var sql = """
                SELECT TO_CHAR(e.created_at, 'YYYY-MM') AS period,
                       COALESCE(SUM(l.credit) FILTER (WHERE l.account_code = 'REVENUE'), 0)
                         - COALESCE(SUM(l.debit) FILTER (WHERE l.account_code = 'REVENUE'), 0) AS net_revenue,
                       COALESCE(SUM(l.credit) FILTER (WHERE l.account_code = 'TAX_PAYABLE'), 0)
                         - COALESCE(SUM(l.debit) FILTER (WHERE l.account_code = 'TAX_PAYABLE'), 0) AS net_tax,
                       COUNT(DISTINCT e.entry_id) AS entry_count
                FROM journal_entries e
                JOIN journal_lines l ON l.entry_id = e.entry_id
                WHERE e.tenant_id = :tenantId
                  AND (CAST(:currency AS VARCHAR) IS NULL OR e.currency = CAST(:currency AS VARCHAR))
                  AND (CAST(:fromDate AS TIMESTAMPTZ) IS NULL OR e.created_at >= CAST(:fromDate AS TIMESTAMPTZ))
                  AND (CAST(:toDate AS TIMESTAMPTZ) IS NULL OR e.created_at <= CAST(:toDate AS TIMESTAMPTZ))
                GROUP BY TO_CHAR(e.created_at, 'YYYY-MM')
                ORDER BY period
                """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("tenantId", TenantContext.getCurrentTenant())
                .setParameter("currency", currency)
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getResultList();

        return rows.stream()
                .map(r -> new RevenueRow(
                        (String) r[0],
                        toBigDecimal(r[1]),
                        toBigDecimal(r[2]),
                        ((Number) r[3]).longValue()
                ))
                .toList();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return BigDecimal.ZERO;
    }
}
