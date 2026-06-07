package com.caloriepilot.api.modules.weight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {

    Optional<WeightLog> findByUserIdAndLogDate(UUID userId, LocalDate logDate);

    List<WeightLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(UUID userId, LocalDate from, LocalDate to);

    Optional<WeightLog> findFirstByUserIdOrderByLogDateAsc(UUID userId);
    Optional<WeightLog> findFirstByUserIdOrderByLogDateDesc(UUID userId);
}
