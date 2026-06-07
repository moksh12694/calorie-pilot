package com.caloriepilot.api.modules.auth.dto;

import com.caloriepilot.api.modules.user.dto.UserResponse;

public record AuthResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {}
