package com.caloriepilot.api.modules.steps;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StepLogRepository extends JpaRepository<StepLog, Long> {
    Optional<StepLog> findByUserIdAndLogDate(UUID userId, LocalDate logDate);
    List<StepLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(UUID userId, LocalDate from, LocalDate to);
}
