package com.caloriepilot.api.modules.notifications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    boolean existsByUserIdAndLogDateAndKind(UUID userId, LocalDate logDate, NotificationKind kind);
}
