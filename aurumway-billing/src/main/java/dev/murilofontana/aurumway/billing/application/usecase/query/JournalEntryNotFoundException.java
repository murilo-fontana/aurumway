package dev.murilofontana.aurumway.billing.application.usecase.query;

public class JournalEntryNotFoundException extends RuntimeException {

    public JournalEntryNotFoundException(String entryId) {
        super("Journal entry not found: " + entryId);
    }
}
