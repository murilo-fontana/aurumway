ALTER TABLE payments ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
ALTER TABLE idempotency_keys ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';

CREATE INDEX idx_payments_tenant ON payments(tenant_id);
