package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.ImportStatementUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.service.CsvStatementParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
public class ImportStatementHandler implements ImportStatementUseCase {

    private final StatementRepositoryPort repository;

    public ImportStatementHandler(StatementRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ImportStatementResult execute(String bankAccount, String filename, InputStream csvData) {
        var transactions = CsvStatementParser.parse(csvData);
        var statement = BankStatement.create(bankAccount, filename, transactions);
        var saved = repository.save(statement);

        return new ImportStatementResult(
                saved.id().value(), saved.status().name(),
                saved.transactions().size(), saved.importedAt()
        );
    }
}
