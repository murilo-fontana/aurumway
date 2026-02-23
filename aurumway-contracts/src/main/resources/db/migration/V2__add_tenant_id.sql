ALTER TABLE contracts ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE contract_items ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';

CREATE INDEX idx_contracts_tenant ON contracts(tenant_id);
