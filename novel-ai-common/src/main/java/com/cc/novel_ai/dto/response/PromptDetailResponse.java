package com.cc.novel_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 提示词详情响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String content;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 用户信息
     */
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
}
