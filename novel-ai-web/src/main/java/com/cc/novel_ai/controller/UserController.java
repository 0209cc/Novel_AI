package com.cc.novel_ai.controller;

import com.cc.novel_ai.dto.request.UserUpdateRequest;
import com.cc.novel_ai.dto.response.ApiResponse;
import com.cc.novel_ai.dto.response.UserProfileResponse;
import com.cc.novel_ai.service.FileStorageService;
import com.cc.novel_ai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        UserProfileResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新当前用户信息（昵称、头像）
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse response = userService.updateCurrentUser(request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", response));
    }

    /**
     * 更新头像（JSON方式，直接传头像URL）
     */
    @PutMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateAvatar(
            @RequestBody Map<String, String> body) {
        String avatarUrl = body.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new com.cc.novel_ai.exception.BadRequestException("头像URL不能为空");
        }
        UserProfileResponse response = userService.updateAvatar(avatarUrl);
        return ResponseEntity.ok(ApiResponse.success("头像更新成功", response));
    }

    /**
     * 上传头像（multipart文件上传）
     */
    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadAvatar(
            @RequestParam("avatar") MultipartFile avatar) {
        String avatarUrl = fileStorageService.storeFile(avatar);
        UserProfileResponse response = userService.updateAvatar(avatarUrl);
        return ResponseEntity.ok(ApiResponse.success("头像上传成功", response));
    }
}
