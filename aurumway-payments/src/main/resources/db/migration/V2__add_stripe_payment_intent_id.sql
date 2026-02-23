ALTER TABLE payments
    ADD COLUMN stripe_payment_intent_id VARCHAR(255);

CREATE INDEX idx_payments_stripe_pi ON payments (stripe_payment_intent_id)
    WHERE stripe_payment_intent_id IS NOT NULL;
