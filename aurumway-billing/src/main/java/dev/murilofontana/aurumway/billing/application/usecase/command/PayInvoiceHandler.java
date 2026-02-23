package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.PayInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayInvoiceHandler implements PayInvoiceUseCase {

    private final InvoiceRepositoryPort repository;
    private final LedgerRepositoryPort ledger;

    public PayInvoiceHandler(InvoiceRepositoryPort repository, LedgerRepositoryPort ledger) {
        this.repository = repository;
        this.ledger = ledger;
    }

    @Override
    @Transactional
    public PayInvoiceResult execute(String invoiceId) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        invoice.markPaid();
        repository.save(invoice);

        var entryNumber = ledger.nextEntryNumber();
        var journalEntry = LedgerService.forPaymentReceived(entryNumber, invoice);
        ledger.save(journalEntry);

        return new PayInvoiceResult(invoice.id().value(), invoice.status().name());
    }
}
