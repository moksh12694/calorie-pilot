package com.caloriepilot.api.modules.notifications;

import com.caloriepilot.api.modules.notifications.dto.PushDeviceResponse;
import com.caloriepilot.api.modules.notifications.dto.PushRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushDeviceService {

    private final PushDeviceRepository repository;

    @Transactional
    public PushDeviceResponse register(UUID userId, PushRegisterRequest req) {
        PushDevice device = repository.findByUserIdAndExpoPushToken(userId, req.expoPushToken())
                .orElseGet(() -> PushDevice.builder()
                        .userId(userId)
                        .expoPushToken(req.expoPushToken())
                        .platform(req.platform())
                        .build());
        device.setPlatform(req.platform());
        device.setLastSeenAt(Instant.now());
        device = repository.save(device);
        return new PushDeviceResponse(device.getId(), device.getExpoPushToken(), device.getPlatform(), device.getLastSeenAt());
    }

    @Transactional
    public void unregister(UUID userId, String expoPushToken) {
        repository.findByUserIdAndExpoPushToken(userId, expoPushToken)
                .ifPresent(repository::delete);
    }
}
