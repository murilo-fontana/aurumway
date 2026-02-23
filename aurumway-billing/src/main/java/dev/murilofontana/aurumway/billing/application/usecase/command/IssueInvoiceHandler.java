package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.IssueInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class IssueInvoiceHandler implements IssueInvoiceUseCase {

    private final InvoiceRepositoryPort repository;
    private final LedgerRepositoryPort ledger;

    public IssueInvoiceHandler(InvoiceRepositoryPort repository, LedgerRepositoryPort ledger) {
        this.repository = repository;
        this.ledger = ledger;
    }

    @Override
    @Transactional
    public IssueInvoiceResult execute(String invoiceId, LocalDate dueDate) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        var invoiceNumber = repository.nextInvoiceNumber(LocalDate.now().getYear());
        invoice.issue(invoiceNumber, dueDate);
        repository.save(invoice);

        var entryNumber = ledger.nextEntryNumber();
        var journalEntry = LedgerService.forInvoiceIssued(entryNumber, invoice);
        ledger.save(journalEntry);

        return new IssueInvoiceResult(invoice.id().value(), invoice.invoiceNumber(), invoice.status().name());
    }
}
