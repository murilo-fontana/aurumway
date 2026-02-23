package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.AgingReportUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.ReportQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class AgingReportHandler implements AgingReportUseCase {

    private static final String CURRENT = "Current";
    private static final String DAYS_1_30 = "1-30 days";
    private static final String DAYS_31_60 = "31-60 days";
    private static final String DAYS_61_90 = "61-90 days";
    private static final String DAYS_OVER_90 = "90+ days";

    private final ReportQueryPort reportQuery;

    public AgingReportHandler(ReportQueryPort reportQuery) {
        this.reportQuery = reportQuery;
    }

    @Override
    @Transactional(readOnly = true)
    public AgingReportResult execute(String currency) {
        var today = LocalDate.now();
        var invoices = reportQuery.agingReport(currency, today);

        var bucketMap = new LinkedHashMap<String, BucketAccumulator>();
        bucketMap.put(CURRENT, new BucketAccumulator());
        bucketMap.put(DAYS_1_30, new BucketAccumulator());
        bucketMap.put(DAYS_31_60, new BucketAccumulator());
        bucketMap.put(DAYS_61_90, new BucketAccumulator());
        bucketMap.put(DAYS_OVER_90, new BucketAccumulator());

        for (var inv : invoices) {
            var bucket = classifyBucket(inv.dueDate(), today);
            var acc = bucketMap.get(bucket);
            acc.count++;
            acc.amount = acc.amount.add(inv.totalAmount());
        }

        var buckets = bucketMap.entrySet().stream()
                .map(e -> new AgingBucket(e.getKey(), e.getValue().count, e.getValue().amount))
                .collect(Collectors.toList());

        var totalOutstanding = buckets.stream()
                .map(AgingBucket::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AgingReportResult(currency != null ? currency : "ALL", buckets, totalOutstanding);
    }

    private String classifyBucket(LocalDate dueDate, LocalDate today) {
        if (dueDate == null || !dueDate.isBefore(today)) {
            return CURRENT;
        }
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
        if (daysOverdue <= 30) return DAYS_1_30;
        if (daysOverdue <= 60) return DAYS_31_60;
        if (daysOverdue <= 90) return DAYS_61_90;
        return DAYS_OVER_90;
    }

    private static class BucketAccumulator {
        int count = 0;
        BigDecimal amount = BigDecimal.ZERO;
    }
}
