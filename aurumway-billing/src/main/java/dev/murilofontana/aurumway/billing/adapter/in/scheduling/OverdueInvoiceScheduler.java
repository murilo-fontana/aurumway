package dev.murilofontana.aurumway.billing.adapter.in.scheduling;

import dev.murilofontana.aurumway.billing.application.port.in.MarkOverdueInvoicesUseCase;
import dev.murilofontana.aurumway.billing.config.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/**
 * Transitions SENT invoices past their due date to OVERDUE, once per tenant.
 */
@Component
@ConditionalOnProperty(prefix = "scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OverdueInvoiceScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueInvoiceScheduler.class);

    private final MarkOverdueInvoicesUseCase markOverdue;
    private final Set<String> tenants;

    public OverdueInvoiceScheduler(MarkOverdueInvoicesUseCase markOverdue,
                                   @Value("${app.allowed-tenants}") Set<String> tenants) {
        this.markOverdue = markOverdue;
        this.tenants = tenants;
    }

    @Scheduled(cron = "${scheduling.mark-overdue.cron}")
    public void markOverdueInvoices() {
        var today = LocalDate.now();
        for (var tenant : tenants) {
            TenantContext.setCurrentTenant(tenant);
            try {
                int count = markOverdue.execute(today);
                if (count > 0) {
                    log.info("Tenant {}: marked {} invoice(s) as OVERDUE", tenant, count);
                }
            } catch (Exception e) {
                log.error("Mark-overdue job failed for tenant {}: {}", tenant, e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
    }
}
