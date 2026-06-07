package com.caloriepilot.api.modules.notifications;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.notifications.dto.PushDeviceResponse;
import com.caloriepilot.api.modules.notifications.dto.PushRegisterRequest;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Push")
@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushDeviceController {

    private final PushDeviceService service;

    @PostMapping("/register")
    public ApiResponse<PushDeviceResponse> register(@CurrentUser AuthenticatedUser user,
                                                    @Valid @RequestBody PushRegisterRequest req) {
        return ApiResponse.ok(service.register(user.id(), req));
    }

    @DeleteMapping("/register")
    public ApiResponse<Void> unregister(@CurrentUser AuthenticatedUser user,
                                        @RequestParam String token) {
        service.unregister(user.id(), token);
        return ApiResponse.ok(null);
    }
}
