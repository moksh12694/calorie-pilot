package com.caloriepilot.api.modules.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        LocalDate dateOfBirth,
        String gender,
        BigDecimal heightCm,
        String timezone
) {}
