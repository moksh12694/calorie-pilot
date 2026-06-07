package com.caloriepilot.api.modules.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByUserIdAndLogDateOrderByMealAscIdAsc(UUID userId, LocalDate logDate);

    List<MealEntry> findByUserIdAndLogDateBetween(UUID userId, LocalDate from, LocalDate to);

    Optional<MealEntry> findByIdAndUserId(Long id, UUID userId);

    @Query("""
           SELECT new com.caloriepilot.api.modules.food.MealEntryRepository$DailyTotals(
              COALESCE(SUM(m.calories), 0),
              COALESCE(SUM(m.proteinG), 0),
              COALESCE(SUM(m.carbsG),   0),
              COALESCE(SUM(m.fatG),     0)
           )
           FROM MealEntry m
           WHERE m.userId = :userId AND m.logDate = :date
           """)
    DailyTotals sumForDay(@Param("userId") UUID userId, @Param("date") LocalDate date);

    record DailyTotals(BigDecimal calories, BigDecimal protein, BigDecimal carbs, BigDecimal fat) {}
}
