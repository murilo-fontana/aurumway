package dev.murilofontana.aurumway.contracts.adapter.in.scheduling;

import dev.murilofontana.aurumway.contracts.application.port.in.GenerateInvoicesUseCase;
import dev.murilofontana.aurumway.contracts.config.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Periodically generates invoices for contracts due for billing, once per tenant.
 * Mirrors the manual {@code POST /billing/generate-invoices} endpoint.
 */
@Component
@ConditionalOnProperty(prefix = "scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RecurringInvoiceScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringInvoiceScheduler.class);

    private final GenerateInvoicesUseCase generateInvoices;
    private final Set<String> tenants;

    public RecurringInvoiceScheduler(GenerateInvoicesUseCase generateInvoices,
                                     @Value("${app.allowed-tenants}") Set<String> tenants) {
        this.generateInvoices = generateInvoices;
        this.tenants = tenants;
    }

    @Scheduled(cron = "${scheduling.recurring-invoices.cron}")
    public void generateDueInvoices() {
        log.info("Running recurring invoice generation for {} tenant(s)", tenants.size());
        for (var tenant : tenants) {
            TenantContext.setCurrentTenant(tenant);
            try {
                var result = generateInvoices.execute();
                if (result.invoicesGenerated() > 0) {
                    log.info("Tenant {}: generated {} invoice(s) from {} due contract(s)",
                            tenant, result.invoicesGenerated(), result.contractsProcessed());
                }
            } catch (Exception e) {
                log.error("Recurring invoice generation failed for tenant {}: {}", tenant, e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }
    }
}
