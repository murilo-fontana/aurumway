package dev.murilofontana.aurumway.contracts.adapter.out.integration;

import dev.murilofontana.aurumway.contracts.application.port.out.BillingServicePort;
import dev.murilofontana.aurumway.contracts.config.JwtService;
import dev.murilofontana.aurumway.contracts.config.TenantContext;
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
                                 JwtService jwtService) {
        this.jwtService = jwtService;
        this.restClient = RestClient.builder()
                .baseUrl(billingUrl)
                .build();
    }

    @Override
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
