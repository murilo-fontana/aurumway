package dev.murilofontana.aurumway.contracts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Reports reachability of the downstream Billing service (contracts calls it to
 * generate invoices). Excluded from the readiness group so a Billing outage does
 * not depool this instance; it only surfaces in the aggregate health.
 */
@Component("billingService")
public class BillingServiceHealthIndicator implements HealthIndicator {

    private final RestClient restClient;

    public BillingServiceHealthIndicator(@Value("${billing.service.url}") String billingUrl) {
        this.restClient = RestClient.builder().baseUrl(billingUrl).build();
    }

    @Override
    public Health health() {
        try {
            var status = restClient.get()
                    .uri("/actuator/health/liveness")
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();
            if (status.is2xxSuccessful()) {
                return Health.up().withDetail("billingService", "reachable").build();
            }
            return Health.down().withDetail("billingService", "status " + status.value()).build();
        } catch (Exception e) {
            return Health.down().withDetail("billingService", "unreachable").withException(e).build();
        }
    }
}
