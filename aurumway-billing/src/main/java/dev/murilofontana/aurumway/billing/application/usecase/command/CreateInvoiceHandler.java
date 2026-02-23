package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.CreateInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.CustomerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.InvoiceLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateInvoiceHandler implements CreateInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepository;
    private final CustomerRepositoryPort customerRepository;

    public CreateInvoiceHandler(InvoiceRepositoryPort invoiceRepository, CustomerRepositoryPort customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CreateInvoiceResult execute(CreateInvoiceCommand command) {
        var customerId = new CustomerId(command.customerId());
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + command.customerId());
        }

        var lines = command.lines().stream()
                .map(li -> InvoiceLine.create(li.description(), li.quantity(), li.unitPrice(), li.taxRate()))
                .toList();

        var invoice = Invoice.createDraft(customerId, command.currency(), lines);
        var saved = invoiceRepository.save(invoice);

        return new CreateInvoiceResult(
                saved.id().value(),
                saved.status().name(),
                saved.totalAmount().amount().toPlainString(),
                saved.currency()
        );
    }
}
