package com.caloriepilot.api.modules.analytics;

import com.caloriepilot.api.modules.analytics.dto.AnalyticsRange;
import com.caloriepilot.api.modules.analytics.dto.Point;
import com.caloriepilot.api.modules.food.MealEntry;
import com.caloriepilot.api.modules.food.MealEntryRepository;
import com.caloriepilot.api.modules.steps.StepLog;
import com.caloriepilot.api.modules.steps.StepLogRepository;
import com.caloriepilot.api.modules.water.WaterLog;
import com.caloriepilot.api.modules.water.WaterLogRepository;
import com.caloriepilot.api.modules.weight.WeightLog;
import com.caloriepilot.api.modules.weight.WeightLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final StepLogRepository stepRepo;
    private final MealEntryRepository mealRepo;
    private final WaterLogRepository waterRepo;
    private final WeightLogRepository weightRepo;

    @Transactional(readOnly = true)
    public AnalyticsRange daily(UUID userId, LocalDate date) {
        return range(userId, date, date);
    }

    @Transactional(readOnly = true)
    public AnalyticsRange weekly(UUID userId, LocalDate anchor) {
        LocalDate start = anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end   = start.plusDays(6);
        return range(userId, start, end);
    }

    @Transactional(readOnly = true)
    public AnalyticsRange monthly(UUID userId, LocalDate anchor) {
        LocalDate start = anchor.withDayOfMonth(1);
        LocalDate end   = anchor.with(TemporalAdjusters.lastDayOfMonth());
        return range(userId, start, end);
    }

    private AnalyticsRange range(UUID userId, LocalDate from, LocalDate to) {
        List<StepLog>  steps  = stepRepo.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);
        List<MealEntry> meals = mealRepo.findByUserIdAndLogDateBetween(userId, from, to);
        List<WeightLog> weights = weightRepo.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);
        // Water: use the repository's date list query (per-day) via stream group.
        List<WaterLog> waters = waterDailyList(userId, from, to);

        // Steps series
        List<Point> stepsSeries = steps.stream()
                .map(s -> new Point(s.getLogDate(), BigDecimal.valueOf(s.getSteps())))
                .toList();

        // Calories series — sum meal calories per day
        Map<LocalDate, BigDecimal> kcalByDay = meals.stream()
                .collect(Collectors.groupingBy(MealEntry::getLogDate,
                        Collectors.reducing(BigDecimal.ZERO, MealEntry::getCalories, BigDecimal::add)));
        List<Point> caloriesSeries = kcalByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new Point(e.getKey(), e.getValue()))
                .toList();

        // Water series — sum ml per day
        Map<LocalDate, Integer> mlByDay = waters.stream()
                .collect(Collectors.groupingBy(WaterLog::getLogDate,
                        Collectors.summingInt(WaterLog::getAmountMl)));
        List<Point> waterSeries = mlByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new Point(e.getKey(), BigDecimal.valueOf(e.getValue())))
                .toList();

        // Weight series
        List<Point> weightSeries = weights.stream()
                .map(w -> new Point(w.getLogDate(), w.getWeightKg()))
                .toList();

        // Totals
        long stepsTotal = steps.stream().mapToLong(StepLog::getSteps).sum();
        BigDecimal caloriesTotal = kcalByDay.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        long waterTotal = mlByDay.values().stream().mapToLong(Integer::longValue).sum();

        // Averages (over days with data)
        int stepDays  = (int) stepsSeries.size();
        int kcalDays  = (int) caloriesSeries.size();
        int waterDays = (int) waterSeries.size();
        AnalyticsRange.Averages averages = new AnalyticsRange.Averages(
                avg(stepsTotal,   stepDays),
                avg(caloriesTotal, kcalDays),
                avg(waterTotal,    waterDays),
                weights.isEmpty() ? null : avg(weights.stream()
                        .map(WeightLog::getWeightKg).reduce(BigDecimal.ZERO, BigDecimal::add), weights.size())
        );

        AnalyticsRange.Totals totals = new AnalyticsRange.Totals(stepsTotal, caloriesTotal, waterTotal);
        AnalyticsRange.Series series = new AnalyticsRange.Series(stepsSeries, caloriesSeries, waterSeries, weightSeries);
        return new AnalyticsRange(from, to, totals, averages, series);
    }

    // Use a per-day expanding loop to leverage the existing sumForDay; cheap for the bounded ranges we expose.
    private List<WaterLog> waterDailyList(UUID userId, LocalDate from, LocalDate to) {
        // Reuse the repository's daily-list query by date if available; here we approximate with the existing
        // findByUserIdAndLogDateOrderByLoggedAtAsc but as a batch is more efficient — push into repo if needed.
        List<WaterLog> all = new ArrayList<>();
        LocalDate d = from;
        while (!d.isAfter(to)) {
            all.addAll(waterRepo.findByUserIdAndLogDateOrderByLoggedAtAsc(userId, d));
            d = d.plusDays(1);
        }
        return all;
    }

    private static BigDecimal avg(long total, int days) {
        if (days == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(total).divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal avg(BigDecimal total, int days) {
        if (days == 0) return BigDecimal.ZERO;
        return total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
    }
}
