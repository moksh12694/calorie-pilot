package com.caloriepilot.api.modules.water;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.achievements.AchievementService;
import com.caloriepilot.api.modules.user.UserGoal;
import com.caloriepilot.api.modules.user.UserGoalRepository;
import com.caloriepilot.api.modules.water.dto.WaterDayResponse;
import com.caloriepilot.api.modules.water.dto.WaterEntry;
import com.caloriepilot.api.modules.water.dto.WaterLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaterService {

    private final WaterLogRepository repository;
    private final UserGoalRepository userGoalRepository;
    private final AchievementService achievementService;

    @Transactional
    public WaterEntry log(UUID userId, WaterLogRequest req) {
        WaterLog saved = repository.save(WaterLog.builder()
                .userId(userId)
                .logDate(req.date())
                .amountMl(req.amountMl())
                .build());

        // Achievement: hit the daily water goal in a single day
        int total = repository.sumForDay(userId, req.date());
        userGoalRepository.findById(userId).ifPresent(g -> {
            if (total >= g.getDailyWaterMl()) {
                achievementService.onWaterGoalMet(userId);
            }
        });

        return new WaterEntry(saved.getId(), saved.getAmountMl(), saved.getLoggedAt());
    }

    @Transactional
    public void deleteEntry(UUID userId, Long id) {
        WaterLog entry = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.notFound("Water entry"));
        repository.delete(entry);
    }

    @Transactional(readOnly = true)
    public WaterDayResponse day(UUID userId, LocalDate date) {
        UserGoal goal = userGoalRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Goals"));
        int total = repository.sumForDay(userId, date);
        List<WaterEntry> entries = repository.findByUserIdAndLogDateOrderByLoggedAtAsc(userId, date).stream()
                .map(w -> new WaterEntry(w.getId(), w.getAmountMl(), w.getLoggedAt()))
                .toList();
        int pct = goal.getDailyWaterMl() == 0
                ? 0 : Math.min(999, (int) Math.round(100.0 * total / goal.getDailyWaterMl()));
        return new WaterDayResponse(date, total, goal.getDailyWaterMl(), pct, entries);
    }
}
