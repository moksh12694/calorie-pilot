package com.caloriepilot.api.modules.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sends pushes via Expo's push service.
 * https://docs.expo.dev/push-notifications/sending-notifications/
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpoPushService {

    private final ObjectMapper objectMapper;
    private RestClient client;

    @Value("${app.notifications.expo-endpoint:https://exp.host/--/api/v2/push/send}")
    private String endpoint;

    private RestClient client() {
        if (client == null) {
            client = RestClient.builder()
                    .baseUrl(endpoint)
                    .defaultHeader("Accept", "application/json")
                    .defaultHeader("Accept-Encoding", "gzip, deflate")
                    .defaultHeader("Content-Type", "application/json")
                    .build();
        }
        return client;
    }

    public void send(List<PushDevice> devices, String title, String body, Map<String, Object> data) {
        if (devices.isEmpty()) return;

        List<Map<String, Object>> messages = new ArrayList<>(devices.size());
        for (PushDevice d : devices) {
            messages.add(Map.of(
                    "to", d.getExpoPushToken(),
                    "sound", "default",
                    "title", title,
                    "body", body,
                    "data", data,
                    "priority", "high"
            ));
        }

        try {
            String response = client().post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(messages)
                    .retrieve()
                    .body(String.class);
            log.debug("Expo push response: {}", response);
        } catch (Exception e) {
            // Don't propagate — notification failures must not break the user-facing request.
            log.warn("Expo push send failed for {} devices: {}", devices.size(), e.getMessage());
        }
    }
}
