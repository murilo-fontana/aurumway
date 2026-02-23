package dev.murilofontana.aurumway.billing.application.usecase.query;

public class StatementNotFoundException extends RuntimeException {

    public StatementNotFoundException(String statementId) {
        super("Statement not found: " + statementId);
    }
}
