package dev.murilofontana.aurumway.contracts.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class TenantFilterSupport {

    @PersistenceContext
    private EntityManager entityManager;

    public void enableTenantFilter() {
        var tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is not set - cannot execute tenant-scoped query");
        }
        entityManager.unwrap(Session.class)
                .enableFilter("tenantFilter")
                .setParameter("tenantId", tenantId);
    }
}
