package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.ListStatementsUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListStatementsHandler implements ListStatementsUseCase {

    private final StatementRepositoryPort repository;

    public ListStatementsHandler(StatementRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatementSummary> execute() {
        return repository.findAll().stream()
                .map(s -> new StatementSummary(
                        s.id().value(), s.bankAccount(), s.filename(),
                        s.importedAt(), s.status().name(), s.transactions().size()))
                .toList();
    }
}
