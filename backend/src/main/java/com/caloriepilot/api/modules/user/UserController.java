package com.caloriepilot.api.modules.user;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.user.dto.UserResponse;
import com.caloriepilot.api.modules.user.dto.UserUpdateRequest;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.ok(userService.getMe(user.id()));
    }

    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMe(@CurrentUser AuthenticatedUser user,
                                              @Valid @RequestBody UserUpdateRequest req) {
        return ApiResponse.ok(userService.updateMe(user.id(), req));
    }
}
