package com.caloriepilot.api.modules.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Point(LocalDate date, BigDecimal value) {}
