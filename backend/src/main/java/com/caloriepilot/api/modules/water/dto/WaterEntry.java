package com.caloriepilot.api.modules.water.dto;

import java.time.Instant;

public record WaterEntry(Long id, Integer amountMl, Instant loggedAt) {}
