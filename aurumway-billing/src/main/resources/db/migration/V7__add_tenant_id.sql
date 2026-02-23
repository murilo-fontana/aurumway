ALTER TABLE customers ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE invoices ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE invoice_lines ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE journal_entries ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE journal_lines ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE bank_statements ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE bank_transactions ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';

CREATE INDEX idx_customers_tenant ON customers(tenant_id);
CREATE INDEX idx_invoices_tenant ON invoices(tenant_id);
CREATE INDEX idx_journal_entries_tenant ON journal_entries(tenant_id);
CREATE INDEX idx_bank_statements_tenant ON bank_statements(tenant_id);
