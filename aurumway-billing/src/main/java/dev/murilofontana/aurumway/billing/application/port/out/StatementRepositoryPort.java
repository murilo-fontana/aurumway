package dev.murilofontana.aurumway.billing.application.port.out;

import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;

import java.util.List;
import java.util.Optional;

public interface StatementRepositoryPort {

    BankStatement save(BankStatement statement);

    BankStatement update(BankStatement statement);

    Optional<BankStatement> findById(StatementId id);

    List<BankStatement> findAll();
}
