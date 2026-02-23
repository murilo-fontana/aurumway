CREATE TABLE contracts (
    contract_id     VARCHAR(36)  NOT NULL PRIMARY KEY,
    customer_id     VARCHAR(36)  NOT NULL,
    customer_name   VARCHAR(200) NOT NULL,
    currency        VARCHAR(3)   NOT NULL,
    billing_cycle   VARCHAR(20)  NOT NULL,
    start_date      DATE         NOT NULL,
    end_date        DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    next_billing_date DATE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_contracts_customer ON contracts (customer_id);
CREATE INDEX idx_contracts_status ON contracts (status);
CREATE INDEX idx_contracts_billing ON contracts (next_billing_date) WHERE next_billing_date IS NOT NULL;

CREATE TABLE contract_items (
    item_id     VARCHAR(36)    NOT NULL PRIMARY KEY,
    contract_id VARCHAR(36)    NOT NULL REFERENCES contracts(contract_id),
    description VARCHAR(500)   NOT NULL,
    quantity    INTEGER        NOT NULL,
    unit_price  NUMERIC(19, 2) NOT NULL,
    tax_rate    NUMERIC(5, 4)  NOT NULL DEFAULT 0
);

CREATE INDEX idx_contract_items_contract ON contract_items (contract_id);
