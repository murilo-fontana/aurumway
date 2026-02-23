package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.GetInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetInvoiceHandler implements GetInvoiceUseCase {

    private final InvoiceRepositoryPort repository;

    public GetInvoiceHandler(InvoiceRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetInvoiceResult execute(String invoiceId) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        var lines = invoice.lines().stream()
                .map(l -> new LineResult(l.id().value(), l.description(), l.quantity(),
                        l.unitPrice(), l.taxRate(), l.lineTotal()))
                .toList();

        return new GetInvoiceResult(
                invoice.id().value(), invoice.customerId().value(), invoice.invoiceNumber(),
                invoice.currency(), invoice.totalAmount().amount(), invoice.status().name(),
                invoice.refundedAmount(),
                invoice.issueDate(), invoice.dueDate(), invoice.createdAt(), lines
        );
    }
}
