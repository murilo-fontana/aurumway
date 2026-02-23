package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.CancelInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelInvoiceHandler implements CancelInvoiceUseCase {

    private final InvoiceRepositoryPort repository;
    private final LedgerRepositoryPort ledger;

    public CancelInvoiceHandler(InvoiceRepositoryPort repository, LedgerRepositoryPort ledger) {
        this.repository = repository;
        this.ledger = ledger;
    }

    @Override
    @Transactional
    public CancelInvoiceResult execute(String invoiceId) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        var wasIssued = invoice.status() != InvoiceStatus.DRAFT;

        invoice.cancel();
        repository.save(invoice);

        if (wasIssued) {
            var entryNumber = ledger.nextEntryNumber();
            var journalEntry = LedgerService.forInvoiceCancelled(entryNumber, invoice);
            ledger.save(journalEntry);
        }

        return new CancelInvoiceResult(invoice.id().value(), invoice.status().name());
    }
}
