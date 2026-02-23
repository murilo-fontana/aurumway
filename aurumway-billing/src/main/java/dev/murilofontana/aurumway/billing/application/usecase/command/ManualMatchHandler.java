package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.ManualMatchUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.application.usecase.query.StatementNotFoundException;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import dev.murilofontana.aurumway.billing.domain.valueobject.ReconciliationStatus;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManualMatchHandler implements ManualMatchUseCase {

    private final StatementRepositoryPort statementRepo;
    private final InvoiceRepositoryPort invoiceRepo;
    private final LedgerRepositoryPort ledgerRepo;

    public ManualMatchHandler(StatementRepositoryPort statementRepo,
                              InvoiceRepositoryPort invoiceRepo,
                              LedgerRepositoryPort ledgerRepo) {
        this.statementRepo = statementRepo;
        this.invoiceRepo = invoiceRepo;
        this.ledgerRepo = ledgerRepo;
    }

    @Override
    @Transactional
    public ManualMatchResult execute(String statementId, String transactionId, String invoiceId) {
        var statement = statementRepo.findById(new StatementId(statementId))
                .orElseThrow(() -> new StatementNotFoundException(statementId));

        var transaction = statement.transactions().stream()
                .filter(tx -> tx.id().value().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        var invoice = invoiceRepo.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        transaction.matchToInvoice(invoiceId, ReconciliationStatus.MANUALLY_MATCHED);
        statement.refreshStatus();

        invoice.markPaid();
        invoiceRepo.save(invoice);

        var entryNumber = ledgerRepo.nextEntryNumber();
        var journalEntry = LedgerService.forPaymentReceived(entryNumber, invoice);
        ledgerRepo.save(journalEntry);

        statementRepo.update(statement);

        return new ManualMatchResult(transactionId, invoiceId, "MANUALLY_MATCHED");
    }
}
