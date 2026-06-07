package com.caloriepilot.api.modules.weight;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.achievements.AchievementService;
import com.caloriepilot.api.modules.user.UserGoal;
import com.caloriepilot.api.modules.user.UserGoalRepository;
import com.caloriepilot.api.modules.weight.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeightService {

    private final WeightLogRepository repository;
    private final UserGoalRepository userGoalRepository;
    private final AchievementService achievementService;

    @Transactional
    public WeightEntry log(UUID userId, WeightLogRequest req) {
        WeightLog log = repository.findByUserIdAndLogDate(userId, req.date())
                .orElseGet(() -> WeightLog.builder().userId(userId).logDate(req.date()).build());
        log.setWeightKg(req.weightKg());
        log.setNote(req.note());
        log = repository.save(log);
        achievementService.onFirstWeight(userId);
        return new WeightEntry(log.getId(), log.getLogDate(), log.getWeightKg(), log.getNote());
    }

    @Transactional
    public void delete(UUID userId, Long id) {
        WeightLog log = repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Weight entry"));
        if (!userId.equals(log.getUserId())) throw ApiException.forbidden("Not your entry");
        repository.delete(log);
    }

    @Transactional(readOnly = true)
    public WeightHistoryResponse history(UUID userId, LocalDate from, LocalDate to) {
        List<WeightLog> logs = repository.findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);
        List<WeightEntry> entries = logs.stream()
                .map(l -> new WeightEntry(l.getId(), l.getLogDate(), l.getWeightKg(), l.getNote()))
                .toList();

        BigDecimal earliest = logs.isEmpty() ? null : logs.get(0).getWeightKg();
        BigDecimal latest   = logs.isEmpty() ? null : logs.get(logs.size() - 1).getWeightKg();
        BigDecimal delta    = (earliest != null && latest != null) ? latest.subtract(earliest) : null;
        BigDecimal target   = userGoalRepository.findById(userId)
                .map(UserGoal::getTargetWeightKg).orElse(null);

        return new WeightHistoryResponse(latest, earliest, delta, target, entries);
    }
}
