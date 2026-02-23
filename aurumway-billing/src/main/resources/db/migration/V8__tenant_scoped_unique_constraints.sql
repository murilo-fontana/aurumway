DROP INDEX IF EXISTS idx_invoices_number;
CREATE UNIQUE INDEX idx_invoices_tenant_number ON invoices (tenant_id, invoice_number) WHERE invoice_number IS NOT NULL;

ALTER TABLE journal_entries DROP CONSTRAINT IF EXISTS journal_entries_entry_number_key;
CREATE UNIQUE INDEX idx_journal_entries_tenant_number ON journal_entries (tenant_id, entry_number);
