package dev.murilofontana.aurumway.billing.adapter.in.api;

import com.fasterxml.jackson.databind.JsonNode;
import dev.murilofontana.aurumway.billing.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class BillingApiIT extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    private String login(String username, String password, String tenantId) {
        var body = """
                {"username":"%s","password":"%s","tenantId":"%s"}
                """.formatted(username, password, tenantId);
        var response = rest.postForEntity("/auth/login", json(body), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().get("token").asText();
    }

    private static HttpEntity<String> json(String body) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> authed(String body, String token) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Void> authed(String token) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    @Test
    void rejectsUnauthenticatedRequest() {
        var response = rest.getForEntity("/invoices/some-id", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void rejectsLoginWithUnknownTenant() {
        var body = """
                {"username":"admin","password":"admin","tenantId":"evil-corp"}
                """;
        var response = rest.postForEntity("/auth/login", json(body), JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void fullInvoiceLifecyclePersistsAndIssues() {
        var token = login("admin", "admin", "acme-corp");

        var customerBody = """
                {"name":"Acme Corp","email":"billing@acme.test","taxId":"TAX-1"}
                """;
        var customerResp = rest.postForEntity("/customers", authed(customerBody, token), JsonNode.class);
        assertThat(customerResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var customerId = customerResp.getBody().get("customerId").asText();

        var invoiceBody = """
                {"customerId":"%s","currency":"USD","lines":[
                    {"description":"Consulting","quantity":2,"unitPrice":100.00,"taxRate":0.10}
                ]}
                """.formatted(customerId);
        var invoiceResp = rest.postForEntity("/invoices", authed(invoiceBody, token), JsonNode.class);
        assertThat(invoiceResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var invoiceId = invoiceResp.getBody().get("invoiceId").asText();
        assertThat(new java.math.BigDecimal(invoiceResp.getBody().get("totalAmount").asText()))
                .isEqualByComparingTo("220.00");

        var issueBody = """
                {"dueDate":"2026-12-31"}
                """;
        var issueResp = rest.postForEntity("/invoices/" + invoiceId + "/issue",
                authed(issueBody, token), JsonNode.class);
        assertThat(issueResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(issueResp.getBody().get("status").asText()).isEqualTo("ISSUED");

        var getResp = rest.exchange("/invoices/" + invoiceId, HttpMethod.GET, authed(token), JsonNode.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody().get("status").asText()).isEqualTo("ISSUED");
        assertThat(getResp.getBody().get("invoiceNumber").asText()).isNotBlank();
    }

    @Test
    void tenantsCannotSeeEachOthersInvoices() {
        var acmeToken = login("admin", "admin", "acme-corp");

        var customerBody = """
                {"name":"Acme","email":"a@acme.test","taxId":"T1"}
                """;
        var customerId = rest.postForEntity("/customers", authed(customerBody, acmeToken), JsonNode.class)
                .getBody().get("customerId").asText();

        var invoiceBody = """
                {"customerId":"%s","currency":"USD","lines":[
                    {"description":"Item","quantity":1,"unitPrice":50.00,"taxRate":0.00}
                ]}
                """.formatted(customerId);
        var invoiceId = rest.postForEntity("/invoices", authed(invoiceBody, acmeToken), JsonNode.class)
                .getBody().get("invoiceId").asText();

        var globexToken = login("admin", "admin", "globex-inc");
        var crossResp = rest.exchange("/invoices/" + invoiceId, HttpMethod.GET,
                authed(globexToken), JsonNode.class);

        assertThat(crossResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void viewerCannotCreateInvoice() {
        var viewerToken = login("viewer", "viewer", "acme-corp");

        var invoiceBody = """
                {"customerId":"x","currency":"USD","lines":[
                    {"description":"Item","quantity":1,"unitPrice":50.00,"taxRate":0.00}
                ]}
                """;
        var response = rest.postForEntity("/invoices", authed(invoiceBody, viewerToken), JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
