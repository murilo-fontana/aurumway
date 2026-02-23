package dev.murilofontana.aurumway.contracts.config;

import dev.murilofontana.aurumway.contracts.application.port.out.AuditEventRepositoryPort;
import dev.murilofontana.aurumway.contracts.domain.model.AuditEvent;
import dev.murilofontana.aurumway.contracts.domain.valueobject.AuditAction;
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
            Map.entry("CreateContractHandler", new AuditMeta(AuditAction.CONTRACT_CREATED, "Contract")),
            Map.entry("ActivateContractHandler", new AuditMeta(AuditAction.CONTRACT_ACTIVATED, "Contract")),
            Map.entry("SuspendContractHandler", new AuditMeta(AuditAction.CONTRACT_SUSPENDED, "Contract")),
            Map.entry("ResumeContractHandler", new AuditMeta(AuditAction.CONTRACT_RESUMED, "Contract")),
            Map.entry("TerminateContractHandler", new AuditMeta(AuditAction.CONTRACT_TERMINATED, "Contract")),
            Map.entry("GenerateInvoicesHandler", new AuditMeta(AuditAction.INVOICES_GENERATED, "Contract"))
    );

    public AuditAspect(AuditEventRepositoryPort auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Around("execution(* dev.murilofontana.aurumway.contracts.application.usecase.command..*Handler.execute(..))")
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
        for (var fieldName : new String[]{"contractId", "invoiceId", "customerId", "statementId", "paymentId"}) {
            try {
                var method = result.getClass().getMethod(fieldName);
                return method.invoke(result).toString();
            } catch (Exception ignored) {}
        }
        return result.toString();
    }

    private record AuditMeta(AuditAction action, String entityType) {}
}
