CREATE TABLE customers (
    customer_id VARCHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    tax_id      VARCHAR(50),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
