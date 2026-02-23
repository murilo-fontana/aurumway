package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.SendInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendInvoiceHandler implements SendInvoiceUseCase {

    private final InvoiceRepositoryPort repository;

    public SendInvoiceHandler(InvoiceRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public SendInvoiceResult execute(String invoiceId) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        invoice.send();
        repository.save(invoice);

        return new SendInvoiceResult(invoice.id().value(), invoice.status().name());
    }
}
