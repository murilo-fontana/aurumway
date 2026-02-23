package dev.murilofontana.aurumway.billing.config;

import dev.murilofontana.aurumway.billing.application.port.out.AuditEventRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.model.AuditEvent;
import dev.murilofontana.aurumway.billing.domain.valueobject.AuditAction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditEventRepositoryPort auditRepo;

    private static final Map<String, AuditMeta> HANDLER_AUDIT_MAP = Map.ofEntries(
            Map.entry("CreateCustomerHandler", new AuditMeta(AuditAction.CUSTOMER_CREATED, "Customer")),
            Map.entry("CreateInvoiceHandler", new AuditMeta(AuditAction.INVOICE_CREATED, "Invoice")),
            Map.entry("IssueInvoiceHandler", new AuditMeta(AuditAction.INVOICE_ISSUED, "Invoice")),
            Map.entry("SendInvoiceHandler", new AuditMeta(AuditAction.INVOICE_SENT, "Invoice")),
            Map.entry("PayInvoiceHandler", new AuditMeta(AuditAction.INVOICE_PAID, "Invoice")),
            Map.entry("CancelInvoiceHandler", new AuditMeta(AuditAction.INVOICE_CANCELLED, "Invoice")),
            Map.entry("RefundInvoiceHandler", new AuditMeta(AuditAction.INVOICE_REFUNDED, "Invoice")),
            Map.entry("ImportStatementHandler", new AuditMeta(AuditAction.STATEMENT_IMPORTED, "BankStatement")),
            Map.entry("AutoReconcileHandler", new AuditMeta(AuditAction.STATEMENT_RECONCILED, "BankStatement"))
    );

    public AuditAspect(AuditEventRepositoryPort auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Around("execution(* dev.murilofontana.aurumway.billing.application.usecase.command..*Handler.execute(..))")
    public Object auditCommandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        var result = joinPoint.proceed();

        try {
            var handlerName = joinPoint.getTarget().getClass().getSimpleName();
            var meta = HANDLER_AUDIT_MAP.get(handlerName);
            if (meta == null) return result;

            var tenantId = TenantContext.getCurrentTenant();
            var actor = extractActor();
            var entityId = extractEntityId(result);

            var event = AuditEvent.create(tenantId, actor, meta.action(), meta.entityType(), entityId, null);
            auditRepo.save(event);
        } catch (Exception e) {
            log.warn("🟥 Failed to record audit event: {}", e.getMessage());
        }

        return result;
    }

    private String extractActor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return auth.getName();
        }
        return "system";
    }

    private String extractEntityId(Object result) {
        if (result == null) return "unknown";
        for (var fieldName : new String[]{"invoiceId", "customerId", "statementId", "contractId", "paymentId"}) {
            try {
                var method = result.getClass().getMethod(fieldName);
                return method.invoke(result).toString();
            } catch (Exception ignored) {}
        }
        return result.toString();
    }

    private record AuditMeta(AuditAction action, String entityType) {}
}
