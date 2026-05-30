package dev.murilofontana.aurumway.contracts.adapter.out.integration;

import dev.murilofontana.aurumway.contracts.application.port.out.BillingServicePort;
import dev.murilofontana.aurumway.contracts.config.JwtService;
import dev.murilofontana.aurumway.contracts.config.TenantContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class BillingServiceAdapter implements BillingServicePort {

    private final RestClient restClient;
    private final JwtService jwtService;

    public BillingServiceAdapter(@Value("${billing.service.url}") String billingUrl,
                                 JwtService jwtService,
                                 RestClient.Builder restClientBuilder) {
        this.jwtService = jwtService;
        this.restClient = restClientBuilder
                .baseUrl(billingUrl)
                .build();
    }

    @Override
    @Retry(name = "billing")
    @CircuitBreaker(name = "billing")
    public CreateInvoiceResponse createInvoice(CreateInvoiceRequest request) {
        var token = jwtService.generateToken("contracts-service", List.of("FINANCE"), TenantContext.getCurrentTenant());
        return restClient.post()
                .uri("/invoices")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .retrieve()
                .body(CreateInvoiceResponse.class);
    }
}
