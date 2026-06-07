package com.caloriepilot.api.steps;

import com.caloriepilot.api.AbstractIntegrationTest;
import com.caloriepilot.api.modules.notifications.NotificationKind;
import com.caloriepilot.api.modules.notifications.NotificationLogRepository;
import com.caloriepilot.api.modules.user.UserRepository;
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

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StepSyncIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort int port;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;
    @Autowired NotificationLogRepository notificationLogRepository;

    @Test
    void sync_is_monotonic_and_dispatches_dedupd_notifications() throws Exception {
        RestTemplate http = new RestTemplateBuilder()
                .rootUri("http://localhost:" + port + "/api")
                .build();

        // signup
        JsonNode signup = postJson(http, "/auth/signup", Map.of(
                "email", "bob@example.com", "password", "password1234", "name", "Bob"));
        String token = signup.get("data").get("accessToken").asText();
        var userId = userRepository.findByEmailIgnoreCase("bob@example.com").orElseThrow().getId();

        LocalDate today = LocalDate.now();

        // Sync 8000 steps → 80% of default 10k goal
        sync(http, token, today, 8000);
        assertThat(notificationLogRepository
                .existsByUserIdAndLogDateAndKind(userId, today, NotificationKind.STEPS_80)).isTrue();
        long firstCount = notificationLogRepository.count();

        // Re-sync same steps — must NOT add new notification rows (dedup)
        sync(http, token, today, 8000);
        assertThat(notificationLogRepository.count()).isEqualTo(firstCount);

        // Sync higher — 12,000 should add 90/95/100 rows (and 12k is below 120% so no EXCEEDED)
        sync(http, token, today, 12000);
        assertThat(notificationLogRepository
                .existsByUserIdAndLogDateAndKind(userId, today, NotificationKind.STEPS_100)).isTrue();

        // Try to decrement — must be ignored (monotonic)
        JsonNode resp = sync(http, token, today, 5000);
        assertThat(resp.get("data").get("steps").asInt()).isEqualTo(12000);
    }

    private JsonNode sync(RestTemplate http, String token, LocalDate date, int steps) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(token);
        var res = http.exchange("/steps/sync", HttpMethod.POST,
                new HttpEntity<>(Map.of("date", date.toString(), "steps", steps), h), String.class);
        return om.readTree(res.getBody());
    }

    private JsonNode postJson(RestTemplate http, String path, Object body) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        var res = http.exchange(path, HttpMethod.POST, new HttpEntity<>(body, h), String.class);
        return om.readTree(res.getBody());
    }
}
