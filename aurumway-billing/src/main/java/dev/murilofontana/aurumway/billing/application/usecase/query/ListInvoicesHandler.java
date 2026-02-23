package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.ListInvoicesUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ListInvoicesHandler implements ListInvoicesUseCase {

    private final InvoiceRepositoryPort repository;

    public ListInvoicesHandler(InvoiceRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceSummary> execute(String status, String customerId, Instant from, Instant to) {
        return repository.findWithFilters(status, customerId, from, to).stream()
                .map(inv -> new InvoiceSummary(
                        inv.id().value(), inv.customerId().value(), inv.invoiceNumber(),
                        inv.currency(), inv.totalAmount().amount(), inv.status().name(),
                        inv.dueDate(), inv.createdAt()
                ))
                .toList();
    }
}
