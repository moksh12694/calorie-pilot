package com.caloriepilot.api.modules.user;

import com.caloriepilot.api.modules.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getHeightCm(),
                user.getTimezone()
        );
    }
}
