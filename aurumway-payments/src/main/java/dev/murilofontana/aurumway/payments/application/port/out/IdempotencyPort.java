package dev.murilofontana.aurumway.payments.application.port.out;

import java.util.Optional;

public interface IdempotencyPort {

    Optional<CachedResponse> find(String idempotencyKey, String endpoint);

    void store(String idempotencyKey, String endpoint, int responseStatus, String responseBody);

    record CachedResponse(int status, String body) {}
}
