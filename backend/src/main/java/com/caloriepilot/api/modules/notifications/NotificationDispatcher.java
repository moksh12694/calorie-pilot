package com.caloriepilot.api.modules.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Decides which step-progress notifications to fire and persists a dedup row before sending.
 * The actual Expo push call is delegated to {@link ExpoPushService}, populated in Module 5.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationLogRepository logRepository;
    private final PushDeviceRepository deviceRepository;
    private final ExpoPushService expoPushService;

    private static final List<Threshold> THRESHOLDS = List.of(
            new Threshold(NotificationKind.STEPS_80,  80, "You're 80% to your step goal — keep moving!"),
            new Threshold(NotificationKind.STEPS_90,  90, "90% there. Almost!"),
            new Threshold(NotificationKind.STEPS_95,  95, "Just one final push — 95%."),
            new Threshold(NotificationKind.STEPS_100,100, "Goal smashed. Treat yourself."),
            new Threshold(NotificationKind.STEPS_EXCEEDED, 120, "20% over goal — you're on fire today.")
    );

    public void dispatchStepThresholds(UUID userId, LocalDate date, int steps, int goal) {
        if (goal <= 0) return;
        int pct = (int) Math.floor(100.0 * steps / goal);
        for (Threshold t : THRESHOLDS) {
            if (pct < t.pct) continue;
            if (logRepository.existsByUserIdAndLogDateAndKind(userId, date, t.kind)) continue;

            // record first so we never double-fire even if the push call retries
            logRepository.save(NotificationLog.builder()
                    .userId(userId).logDate(date).kind(t.kind).build());

            var devices = deviceRepository.findByUserId(userId);
            if (devices.isEmpty()) {
                log.debug("No push devices for user {}, skipping send", userId);
                continue;
            }
            expoPushService.send(devices, "CaloriePilot", t.message,
                    java.util.Map.of("kind", t.kind.name(), "steps", steps, "goal", goal));
        }
    }

    private record Threshold(NotificationKind kind, int pct, String message) {}
}
