package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.CreateInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.CustomerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.IdempotencyPort;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.InvoiceLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateInvoiceHandler implements CreateInvoiceUseCase {

    private static final String ENDPOINT = "POST /invoices";

    private final InvoiceRepositoryPort invoiceRepository;
    private final CustomerRepositoryPort customerRepository;
    private final IdempotencyPort idempotency;

    public CreateInvoiceHandler(InvoiceRepositoryPort invoiceRepository,
                                CustomerRepositoryPort customerRepository,
                                IdempotencyPort idempotency) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.idempotency = idempotency;
    }

    @Override
    @Transactional
    public CreateInvoiceResult execute(CreateInvoiceCommand command) {
        boolean idempotent = command.idempotencyKey() != null && !command.idempotencyKey().isBlank();

        if (idempotent) {
            var replay = idempotency.findResourceId(command.idempotencyKey(), ENDPOINT)
                    .flatMap(id -> invoiceRepository.findById(new InvoiceId(id)))
                    .map(this::toResult);
            if (replay.isPresent()) {
                return replay.get();
            }
        }

        var customerId = new CustomerId(command.customerId());
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + command.customerId());
        }

        var lines = command.lines().stream()
                .map(li -> InvoiceLine.create(li.description(), li.quantity(), li.unitPrice(), li.taxRate()))
                .toList();

        var invoice = Invoice.createDraft(customerId, command.currency(), lines);
        var saved = invoiceRepository.save(invoice);

        if (idempotent) {
            idempotency.store(command.idempotencyKey(), ENDPOINT, saved.id().value());
        }

        return toResult(saved);
    }

    private CreateInvoiceResult toResult(Invoice invoice) {
        return new CreateInvoiceResult(
                invoice.id().value(),
                invoice.status().name(),
                invoice.totalAmount().amount().toPlainString(),
                invoice.currency()
        );
    }
}
