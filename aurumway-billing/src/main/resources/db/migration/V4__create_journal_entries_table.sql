CREATE SEQUENCE journal_entry_number_seq START WITH 1 INCREMENT BY 1 NO CYCLE;

CREATE TABLE journal_entries (
    entry_id     VARCHAR(36)  NOT NULL PRIMARY KEY,
    entry_number VARCHAR(20)  NOT NULL UNIQUE,
    entry_type   VARCHAR(30)  NOT NULL,
    reference_id VARCHAR(36)  NOT NULL,
    currency     VARCHAR(3)   NOT NULL,
    description  VARCHAR(500) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_journal_entries_reference ON journal_entries (reference_id);
CREATE INDEX idx_journal_entries_type ON journal_entries (entry_type);

CREATE TABLE journal_lines (
    line_id      VARCHAR(36)    NOT NULL PRIMARY KEY,
    entry_id     VARCHAR(36)    NOT NULL REFERENCES journal_entries(entry_id),
    account_code VARCHAR(50)    NOT NULL,
    debit        NUMERIC(19, 2) NOT NULL DEFAULT 0,
    credit       NUMERIC(19, 2) NOT NULL DEFAULT 0
);

CREATE INDEX idx_journal_lines_entry ON journal_lines (entry_id);
CREATE INDEX idx_journal_lines_account ON journal_lines (account_code);
