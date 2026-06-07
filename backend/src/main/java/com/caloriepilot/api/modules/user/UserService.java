package com.caloriepilot.api.modules.user;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.user.dto.UserResponse;
import com.caloriepilot.api.modules.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getMe(UUID userId) {
        return userMapper.toResponse(loadUser(userId));
    }

    @Transactional
    public UserResponse updateMe(UUID userId, UserUpdateRequest req) {
        User user = loadUser(userId);
        if (req.name() != null) user.setName(req.name());
        if (req.dateOfBirth() != null) user.setDateOfBirth(req.dateOfBirth());
        if (req.gender() != null) user.setGender(req.gender());
        if (req.heightCm() != null) user.setHeightCm(req.heightCm());
        if (req.timezone() != null) user.setTimezone(req.timezone());
        return userMapper.toResponse(user);
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User"));
    }
}
