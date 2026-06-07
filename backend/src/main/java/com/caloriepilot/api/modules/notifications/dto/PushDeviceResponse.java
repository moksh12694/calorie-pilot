package com.caloriepilot.api.modules.notifications.dto;

import java.time.Instant;

public record PushDeviceResponse(
        Long id,
        String expoPushToken,
        String platform,
        Instant lastSeenAt
) {}
