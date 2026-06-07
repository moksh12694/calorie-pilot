package com.caloriepilot.api.modules.notifications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushDeviceRepository extends JpaRepository<PushDevice, Long> {
    List<PushDevice> findByUserId(UUID userId);
    Optional<PushDevice> findByUserIdAndExpoPushToken(UUID userId, String expoPushToken);
}
