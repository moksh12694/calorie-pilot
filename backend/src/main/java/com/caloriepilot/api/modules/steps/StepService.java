package com.caloriepilot.api.modules.steps;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.achievements.AchievementService;
import com.caloriepilot.api.modules.notifications.NotificationDispatcher;
import com.caloriepilot.api.modules.steps.dto.StepGoalRequest;
import com.caloriepilot.api.modules.steps.dto.StepLogResponse;
import com.caloriepilot.api.modules.steps.dto.StepSyncRequest;
import com.caloriepilot.api.modules.user.UserGoal;
import com.caloriepilot.api.modules.user.UserGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepLogRepository stepLogRepository;
    private final UserGoalRepository userGoalRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final AchievementService achievementService;

    @Transactional
    public StepLogResponse sync(UUID userId, StepSyncRequest req) {
        StepLog log = stepLogRepository.findByUserIdAndLogDate(userId, req.date())
                .orElseGet(() -> StepLog.builder()
                        .userId(userId)
                        .logDate(req.date())
                        .steps(0)
                        .build());
        // Pedometer sync is monotonic — never decrease an existing log.
        if (req.steps() > log.getSteps()) {
            log.setSteps(req.steps());
        }
        if (req.distanceM() != null) log.setDistanceM(req.distanceM());
        if (req.calories()  != null) log.setCalories(req.calories());
        log = stepLogRepository.save(log);

        UserGoal goal = goalFor(userId);
        notificationDispatcher.dispatchStepThresholds(userId, log.getLogDate(), log.getSteps(), goal.getDailyStepGoal());
        achievementService.onStepUpdate(userId, log.getLogDate(), log.getSteps(), goal.getDailyStepGoal());

        return toResponse(log, goal.getDailyStepGoal());
    }

    @Transactional(readOnly = true)
    public StepLogResponse today(UUID userId) {
        LocalDate today = LocalDate.now();
        StepLog log = stepLogRepository.findByUserIdAndLogDate(userId, today)
                .orElseGet(() -> StepLog.builder().userId(userId).logDate(today).steps(0).build());
        return toResponse(log, goalFor(userId).getDailyStepGoal());
    }

    @Transactional(readOnly = true)
    public List<StepLogResponse> history(UUID userId, LocalDate from, LocalDate to) {
        Integer dailyGoal = goalFor(userId).getDailyStepGoal();
        return stepLogRepository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to).stream()
                .map(l -> toResponse(l, dailyGoal))
                .toList();
    }

    @Transactional
    public StepGoalView updateGoal(UUID userId, StepGoalRequest req) {
        UserGoal goal = goalFor(userId);
        goal.setDailyStepGoal(req.dailyStepGoal());
        userGoalRepository.save(goal);
        return new StepGoalView(goal.getDailyStepGoal());
    }

    @Transactional(readOnly = true)
    public StepGoalView getGoal(UUID userId) {
        return new StepGoalView(goalFor(userId).getDailyStepGoal());
    }

    private UserGoal goalFor(UUID userId) {
        return userGoalRepository.findById(userId).orElseThrow(() -> ApiException.notFound("Goals"));
    }

    private StepLogResponse toResponse(StepLog log, Integer dailyGoal) {
        int pct = dailyGoal == 0 ? 0 : Math.min(999, (int) Math.round(100.0 * log.getSteps() / dailyGoal));
        return new StepLogResponse(
                log.getLogDate(),
                log.getSteps(),
                log.getDistanceM(),
                log.getCalories(),
                dailyGoal,
                pct,
                log.getSteps() >= dailyGoal
        );
    }

    public record StepGoalView(Integer dailyStepGoal) {}
}
