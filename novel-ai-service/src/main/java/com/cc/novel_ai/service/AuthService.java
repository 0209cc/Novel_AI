package com.cc.novel_ai.service;

import com.cc.novel_ai.dto.request.LoginRequest;
import com.cc.novel_ai.dto.request.RegisterRequest;
import com.cc.novel_ai.dto.response.AuthResponse;
import com.cc.novel_ai.entity.User;
import com.cc.novel_ai.exception.BadRequestException;
import com.cc.novel_ai.repository.UserRepository;
import com.cc.novel_ai.security.JwtTokenProvider;
import com.cc.novel_ai.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("邮箱已注册");
        }

        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .status(1)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        // 生成 JWT Token
        UserDetailsImpl userDetails = UserDetailsImpl.fromUser(user);
        String token = tokenProvider.generateToken(userDetails);

        // 保存Token到用户记录
        user.setCurrentToken(token);
        userRepository.save(user);

        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    /**
     * 用户登录
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成 JWT Token
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);

        // 保存当前Token到用户记录（单点登录：新登录会覆盖旧Token）
        User user = userRepository.findById(userDetails.getId()).orElseThrow();
        user.setCurrentToken(token);
        userRepository.save(user);

        log.info("User logged in successfully: {}", request.getUsername());

        return new AuthResponse(token, userDetails.getId(), userDetails.getUsername());
    }
}
