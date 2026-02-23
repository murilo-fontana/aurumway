CREATE TABLE invoices (
    invoice_id     VARCHAR(36)    NOT NULL PRIMARY KEY,
    customer_id    VARCHAR(36)    NOT NULL REFERENCES customers(customer_id),
    invoice_number VARCHAR(20),
    currency       VARCHAR(3)     NOT NULL,
    total_amount   NUMERIC(19, 2) NOT NULL DEFAULT 0,
    status         VARCHAR(20)    NOT NULL,
    issue_date     DATE,
    due_date       DATE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_invoices_number ON invoices (invoice_number) WHERE invoice_number IS NOT NULL;

CREATE TABLE invoice_lines (
    line_id     VARCHAR(36)    NOT NULL PRIMARY KEY,
    invoice_id  VARCHAR(36)    NOT NULL REFERENCES invoices(invoice_id) ON DELETE CASCADE,
    description VARCHAR(500)   NOT NULL,
    quantity    INT            NOT NULL,
    unit_price  NUMERIC(19, 2) NOT NULL,
    tax_rate    NUMERIC(5, 4)  NOT NULL DEFAULT 0,
    line_total  NUMERIC(19, 2) NOT NULL
);

CREATE INDEX idx_invoice_lines_invoice ON invoice_lines (invoice_id);
