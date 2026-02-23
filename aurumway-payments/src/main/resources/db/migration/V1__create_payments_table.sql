CREATE TABLE payments (
    payment_id         VARCHAR(36)    NOT NULL PRIMARY KEY,
    external_reference VARCHAR(255)   NOT NULL,
    provider           VARCHAR(50)    NOT NULL,
    amount             NUMERIC(19, 2) NOT NULL,
    currency           VARCHAR(3)     NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now()
);
