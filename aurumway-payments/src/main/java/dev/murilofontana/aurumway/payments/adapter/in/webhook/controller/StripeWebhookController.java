package dev.murilofontana.aurumway.payments.adapter.in.webhook.controller;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import dev.murilofontana.aurumway.payments.application.port.in.HandleStripeWebhookUseCase;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final HandleStripeWebhookUseCase useCase;
    private final String webhookSecret;

    public StripeWebhookController(HandleStripeWebhookUseCase useCase,
                                   @Value("${stripe.webhook-secret:}") String webhookSecret) {
        this.useCase = useCase;
        this.webhookSecret = webhookSecret;
    }

    @PostConstruct
    void validateConfig() {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new IllegalStateException("stripe.webhook-secret must be configured");
        }
    }

    @PostMapping
    public ResponseEntity<String> handle(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            log.warn("Stripe webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        var type = event.getType();
        if (type.equals("payment_intent.succeeded") || type.equals("payment_intent.payment_failed")) {
            var paymentIntent = extractPaymentIntent(event);
            if (paymentIntent != null) {
                useCase.execute(paymentIntent.getId(), type);
            }
        }

        return ResponseEntity.ok("ok");
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isPresent()) {
            return (PaymentIntent) deserializer.getObject().get();
        }

        try {
            StripeObject obj = deserializer.deserializeUnsafe();
            if (obj instanceof PaymentIntent pi) {
                return pi;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
