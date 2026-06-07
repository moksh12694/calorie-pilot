package com.caloriepilot.api.modules.weight.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightLogRequest(
        @NotNull LocalDate date,
        @NotNull @DecimalMin("0.5") @DecimalMax("500.0") BigDecimal weightKg,
        @Size(max = 255) String note
) {}
