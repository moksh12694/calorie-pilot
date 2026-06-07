package com.caloriepilot.api.modules.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PushRegisterRequest(
        @NotBlank String expoPushToken,
        @NotBlank @Pattern(regexp = "^(ios|android|web)$", message = "platform must be ios, android, or web") String platform
) {}
