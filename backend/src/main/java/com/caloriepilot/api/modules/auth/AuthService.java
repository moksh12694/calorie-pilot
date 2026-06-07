package com.caloriepilot.api.modules.auth;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.auth.dto.*;
import com.caloriepilot.api.modules.user.User;
import com.caloriepilot.api.modules.user.UserGoal;
import com.caloriepilot.api.modules.user.UserGoalRepository;
import com.caloriepilot.api.modules.user.UserMapper;
import com.caloriepilot.api.modules.user.UserRepository;
import com.caloriepilot.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse signup(SignupRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw ApiException.conflict("An account with that email already exists");
        }
        User user = User.builder()
                .email(req.email().toLowerCase())
                .name(req.name())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();
        user = userRepository.save(user);

        // Default goals row.
        userGoalRepository.save(UserGoal.builder().userId(user.getId()).build());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest req) {
        UUID userId = jwtService.parseRefreshTokenSubject(req.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.unauthorized("User not found"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String access  = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId());
        return new AuthResponse(
                userMapper.toResponse(user),
                access,
                refresh,
                jwtService.accessTokenTtlSeconds()
        );
    }
}
