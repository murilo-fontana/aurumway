package dev.murilofontana.aurumway.payments.config;

import com.stripe.model.Balance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Reports Stripe connectivity. Excluded from the readiness group so a Stripe
 * outage never depools the instance; it only surfaces in the aggregate health.
 */
@Component("stripe")
public class StripeHealthIndicator implements HealthIndicator {

    private final boolean configured;

    public StripeHealthIndicator(@Value("${stripe.secret-key:}") String secretKey) {
        this.configured = secretKey != null && !secretKey.isBlank();
    }

    @Override
    public Health health() {
        if (!configured) {
            return Health.up().withDetail("stripe", "not-configured").build();
        }
        try {
            Balance.retrieve();
            return Health.up().withDetail("stripe", "reachable").build();
        } catch (Exception e) {
            return Health.down().withDetail("stripe", "unreachable").withException(e).build();
        }
    }
}
