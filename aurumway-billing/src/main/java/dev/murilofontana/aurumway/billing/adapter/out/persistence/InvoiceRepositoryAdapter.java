package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper.InvoicePersistenceMapper;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.repository.JpaInvoiceRepository;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.config.TenantContext;
import dev.murilofontana.aurumway.billing.config.TenantFilterSupport;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final JpaInvoiceRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public InvoiceRepositoryAdapter(JpaInvoiceRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public Invoice save(Invoice invoice) {
        tenantFilter.enableTenantFilter();
        var existing = jpa.findById(invoice.id().value());
        if (existing.isPresent()) {
            var entity = existing.get();
            entity.setStatus(invoice.status().name());
            entity.setInvoiceNumber(invoice.invoiceNumber());
            entity.setIssueDate(invoice.issueDate());
            entity.setDueDate(invoice.dueDate());
            entity.setRefundedAmount(invoice.refundedAmount());
            entity.setTotalAmount(invoice.totalAmount().amount());
            return InvoicePersistenceMapper.toDomain(entity);
        }
        var entity = InvoicePersistenceMapper.toEntity(invoice);
        var saved = jpa.save(entity);
        return InvoicePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByInvoiceId(id.value()).map(InvoicePersistenceMapper::toDomain);
    }

    @Override
    public List<Invoice> findWithFilters(String status, String customerId, Instant from, Instant to) {
        tenantFilter.enableTenantFilter();
        return jpa.findWithFilters(TenantContext.getCurrentTenant(), status, customerId, from, to).stream()
                .map(InvoicePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public String nextInvoiceNumber(int year) {
        long seq = jpa.nextInvoiceNumber();
        return "INV-%d-%06d".formatted(year, seq);
    }
}
