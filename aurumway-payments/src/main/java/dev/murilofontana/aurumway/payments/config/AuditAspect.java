package dev.murilofontana.aurumway.payments.config;

import dev.murilofontana.aurumway.payments.application.port.out.AuditEventRepositoryPort;
import dev.murilofontana.aurumway.payments.domain.model.AuditEvent;
import dev.murilofontana.aurumway.payments.domain.valueobject.AuditAction;
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
            Map.entry("CreatePaymentHandler", new AuditMeta(AuditAction.PAYMENT_CREATED, "Payment")),
            Map.entry("CreatePaymentIntentHandler", new AuditMeta(AuditAction.PAYMENT_INTENT_CREATED, "Payment")),
            Map.entry("HandleStripeWebhookHandler", new AuditMeta(AuditAction.PAYMENT_SUCCEEDED, "Payment")),
            Map.entry("RefundPaymentHandler", new AuditMeta(AuditAction.PAYMENT_REFUNDED, "Payment"))
    );

    public AuditAspect(AuditEventRepositoryPort auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Around("execution(* dev.murilofontana.aurumway.payments.application.usecase.command..*Handler.execute(..)) " +
            "|| execution(* dev.murilofontana.aurumway.payments.application.usecase.command..*Handler.create(..))")
    public Object auditCommandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        var result = joinPoint.proceed();

        try {
            var handlerName = joinPoint.getTarget().getClass().getSimpleName();
            var meta = HANDLER_AUDIT_MAP.get(handlerName);
            if (meta == null) return result;

            var tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                tenantId = "system";
            }
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
        if (result != null) {
            for (var fieldName : new String[]{"paymentId", "invoiceId", "customerId", "statementId", "contractId"}) {
                try {
                    var method = result.getClass().getMethod(fieldName);
                    return method.invoke(result).toString();
                } catch (Exception ignored) {}
            }
            return result.toString();
        }
        return "unknown";
    }

    private record AuditMeta(AuditAction action, String entityType) {}
}
