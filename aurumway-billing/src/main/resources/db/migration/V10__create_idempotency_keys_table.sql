CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(255) NOT NULL PRIMARY KEY,
    endpoint        VARCHAR(255) NOT NULL,
    resource_id     VARCHAR(36)  NOT NULL,
    tenant_id       VARCHAR(50)  NOT NULL DEFAULT 'default',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_idempotency_keys_tenant ON idempotency_keys (tenant_id);
