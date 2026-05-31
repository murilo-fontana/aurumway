package dev.murilofontana.aurumway.billing.application.port.out;

import java.util.Optional;

/**
 * Stores a mapping from a caller-supplied idempotency key to the resource it
 * produced, so a replay of the same request returns the original resource
 * instead of creating a duplicate. Writes participate in the caller's
 * transaction, keeping resource creation and key persistence atomic.
 */
public interface IdempotencyPort {

    Optional<String> findResourceId(String idempotencyKey, String endpoint);

    void store(String idempotencyKey, String endpoint, String resourceId);
}
