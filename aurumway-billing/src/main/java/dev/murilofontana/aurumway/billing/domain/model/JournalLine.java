package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalLineId;

import java.math.BigDecimal;
import java.util.Objects;

public final class JournalLine {

    private final JournalLineId id;
    private final AccountCode accountCode;
    private final BigDecimal debit;
    private final BigDecimal credit;

    private JournalLine(JournalLineId id, AccountCode accountCode, BigDecimal debit, BigDecimal credit) {
        this.id = Objects.requireNonNull(id);
        this.accountCode = Objects.requireNonNull(accountCode);
        this.debit = Objects.requireNonNull(debit);
        this.credit = Objects.requireNonNull(credit);
        if (debit.signum() < 0 || credit.signum() < 0) {
            throw new IllegalArgumentException("Debit and credit must be non-negative");
        }
        if (debit.signum() > 0 && credit.signum() > 0) {
            throw new IllegalArgumentException("A line cannot have both debit and credit");
        }
        if (debit.signum() == 0 && credit.signum() == 0) {
            throw new IllegalArgumentException("A line must have either debit or credit");
        }
    }

    public static JournalLine debit(AccountCode account, BigDecimal amount) {
        return new JournalLine(JournalLineId.newId(), account, amount, BigDecimal.ZERO);
    }

    public static JournalLine credit(AccountCode account, BigDecimal amount) {
        return new JournalLine(JournalLineId.newId(), account, BigDecimal.ZERO, amount);
    }

    public static JournalLine rehydrate(JournalLineId id, AccountCode accountCode, BigDecimal debit, BigDecimal credit) {
        return new JournalLine(id, accountCode, debit, credit);
    }

    public JournalLineId id() { return id; }
    public AccountCode accountCode() { return accountCode; }
    public BigDecimal debit() { return debit; }
    public BigDecimal credit() { return credit; }
}
