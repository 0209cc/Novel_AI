package com.cc.novel_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 提示词列表响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponse {

    private Long id;
    private String title;
    private String description;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime createdAt;

    /**
     * 用户信息
     */
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
}
