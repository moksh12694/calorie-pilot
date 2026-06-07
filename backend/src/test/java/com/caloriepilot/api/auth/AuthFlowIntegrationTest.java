package com.caloriepilot.api.auth;

import com.caloriepilot.api.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthFlowIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort int port;
    @Autowired ObjectMapper om;

    @Test
    void signup_then_login_then_me_should_succeed() throws Exception {
        RestTemplate http = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port + "/api")
                .build();

        // Signup
        JsonNode signup = postJson(http, "/auth/signup", Map.of(
                "email", "alice@example.com",
                "password", "password1234",
                "name", "Alice"
        ));
        assertThat(signup.get("success").asBoolean()).isTrue();
        String accessToken = signup.get("data").get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // Login
        JsonNode login = postJson(http, "/auth/login", Map.of(
                "email", "alice@example.com",
                "password", "password1234"
        ));
        assertThat(login.get("success").asBoolean()).isTrue();

        // /users/me with bearer
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        var me = http.exchange("/users/me", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        JsonNode meBody = om.readTree(me.getBody());
        assertThat(meBody.get("success").asBoolean()).isTrue();
        assertThat(meBody.get("data").get("email").asText()).isEqualTo("alice@example.com");
    }

    @Test
    void me_without_token_returns_401() {
        RestTemplate http = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port + "/api")
                .errorHandler(new org.springframework.web.client.DefaultResponseErrorHandler() {
                    @Override public boolean hasError(org.springframework.http.client.ClientHttpResponse r) { return false; }
                })
                .build();
        var res = http.getForEntity("/users/me", String.class);
        assertThat(res.getStatusCode().value()).isEqualTo(401);
    }

    private JsonNode postJson(RestTemplate http, String path, Object body) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        var res = http.exchange(path, HttpMethod.POST, new HttpEntity<>(body, h), String.class);
        return om.readTree(res.getBody());
    }
}
