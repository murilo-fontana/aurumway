CREATE TABLE outbox_events (
    id              VARCHAR(36) PRIMARY KEY,
    aggregate_type  VARCHAR(50) NOT NULL,
    aggregate_id    VARCHAR(36) NOT NULL,
    event_type      VARCHAR(50) NOT NULL,
    routing_key     VARCHAR(100) NOT NULL,
    payload         TEXT NOT NULL,
    tenant_id       VARCHAR(50),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempts        INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_attempt_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_status_created ON outbox_events(status, created_at);
