package com.caloriepilot.api.modules.water;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {

    List<WaterLog> findByUserIdAndLogDateOrderByLoggedAtAsc(UUID userId, LocalDate logDate);

    Optional<WaterLog> findByIdAndUserId(Long id, UUID userId);

    @Query("SELECT COALESCE(SUM(w.amountMl), 0) FROM WaterLog w WHERE w.userId = :userId AND w.logDate = :date")
    int sumForDay(@Param("userId") UUID userId, @Param("date") LocalDate date);
}
