package com.cc.novel_ai.service;

import com.cc.novel_ai.dto.response.UserProfileResponse;
import com.cc.novel_ai.entity.User;
import com.cc.novel_ai.exception.ResourceNotFoundException;
import com.cc.novel_ai.repository.UserRepository;
import com.cc.novel_ai.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 根据ID获取用户
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    /**
     * 获取当前登录用户
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("用户未登录");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return getUserById(userDetails.getId());
    }

    /**
     * 获取用户资料
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        return mapToUserProfileResponse(user);
    }

    /**
     * 获取当前用户资料
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToUserProfileResponse(user);
    }

    /**
     * 将 User 实体转换为 UserProfileResponse
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
