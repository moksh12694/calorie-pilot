package com.caloriepilot.api.modules.weight.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightEntry(Long id, LocalDate date, BigDecimal weightKg, String note) {}
