CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(255) NOT NULL PRIMARY KEY,
    endpoint        VARCHAR(255) NOT NULL,
    response_status INT          NOT NULL,
    response_body   TEXT         NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
