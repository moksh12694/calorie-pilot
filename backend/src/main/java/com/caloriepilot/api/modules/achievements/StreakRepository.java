package com.caloriepilot.api.modules.achievements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StreakRepository extends JpaRepository<Streak, UUID> {
}
