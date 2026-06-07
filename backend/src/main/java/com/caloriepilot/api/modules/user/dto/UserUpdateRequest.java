package com.caloriepilot.api.modules.user.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserUpdateRequest(
        @Size(max = 120) String name,
        LocalDate dateOfBirth,
        @Size(max = 20) String gender,
        BigDecimal heightCm,
        @Size(max = 64) String timezone
) {}
