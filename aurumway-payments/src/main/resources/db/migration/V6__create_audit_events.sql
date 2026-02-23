CREATE TABLE audit_events (
    event_id    VARCHAR(36) PRIMARY KEY,
    tenant_id   VARCHAR(50) NOT NULL,
    actor       VARCHAR(100) NOT NULL,
    action      VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   VARCHAR(36) NOT NULL,
    payload     TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_events_tenant ON audit_events(tenant_id);
CREATE INDEX idx_audit_events_entity ON audit_events(entity_type, entity_id);
CREATE INDEX idx_audit_events_actor ON audit_events(tenant_id, actor);
CREATE INDEX idx_audit_events_created ON audit_events(tenant_id, created_at);
