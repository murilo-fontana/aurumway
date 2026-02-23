CREATE TABLE bank_statements (
    statement_id VARCHAR(36)  NOT NULL PRIMARY KEY,
    bank_account VARCHAR(100) NOT NULL,
    filename     VARCHAR(255) NOT NULL,
    imported_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    status       VARCHAR(30)  NOT NULL
);

CREATE TABLE bank_transactions (
    transaction_id        VARCHAR(36)    NOT NULL PRIMARY KEY,
    statement_id          VARCHAR(36)    NOT NULL REFERENCES bank_statements(statement_id),
    transaction_date      DATE           NOT NULL,
    amount                NUMERIC(19, 2) NOT NULL,
    currency              VARCHAR(3)     NOT NULL,
    description           VARCHAR(500),
    reference             VARCHAR(200),
    transaction_type      VARCHAR(10)    NOT NULL,
    reconciliation_status VARCHAR(20)    NOT NULL DEFAULT 'UNMATCHED',
    matched_invoice_id    VARCHAR(36)    REFERENCES invoices(invoice_id)
);

CREATE INDEX idx_bank_transactions_statement ON bank_transactions (statement_id);
CREATE INDEX idx_bank_transactions_status ON bank_transactions (reconciliation_status);
CREATE INDEX idx_bank_transactions_matched ON bank_transactions (matched_invoice_id);
