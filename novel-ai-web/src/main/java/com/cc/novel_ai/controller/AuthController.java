package com.cc.novel_ai.controller;

import com.cc.novel_ai.dto.request.LoginRequest;
import com.cc.novel_ai.dto.request.RegisterRequest;
import com.cc.novel_ai.dto.request.SmsRequest;
import com.cc.novel_ai.dto.response.ApiResponse;
import com.cc.novel_ai.dto.response.AuthResponse;
import com.cc.novel_ai.service.AuthService;
import com.cc.novel_ai.service.VerificationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<Object>> sendCode(@Valid @RequestBody SmsRequest request) {
        verificationCodeService.sendCode(request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("验证码发送成功"));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
