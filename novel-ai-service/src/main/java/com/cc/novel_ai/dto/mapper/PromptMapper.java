package com.cc.novel_ai.dto.mapper;

import com.cc.novel_ai.dto.response.PromptDetailResponse;
import com.cc.novel_ai.dto.response.PromptResponse;
import com.cc.novel_ai.entity.Prompt;

/**
 * 提示词对象映射器
 */
public class PromptMapper {

    private PromptMapper() {
        // 私有构造函数，防止实例化
    }

    /**
     * 将 Prompt 实体转换为 PromptResponse（列表展示）
     */
    public static PromptResponse toResponse(Prompt prompt) {
        return PromptResponse.builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .tags(prompt.getTags())
                .viewCount(prompt.getViewCount())
                .likeCount(prompt.getLikeCount())
                .createdAt(prompt.getCreatedAt())
                .userId(prompt.getUser().getId())
                .username(prompt.getUser().getUsername())
                .nickname(prompt.getUser().getNickname())
                .avatarUrl(prompt.getUser().getAvatarUrl())
                .build();
    }

    /**
     * 将 Prompt 实体转换为 PromptDetailResponse（详情展示）
     */
    public static PromptDetailResponse toDetailResponse(Prompt prompt) {
        return PromptDetailResponse.builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .content(prompt.getContent())
                .tags(prompt.getTags())
                .viewCount(prompt.getViewCount())
                .likeCount(prompt.getLikeCount())
                .status(prompt.getStatus())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .userId(prompt.getUser().getId())
                .username(prompt.getUser().getUsername())
                .nickname(prompt.getUser().getNickname())
                .avatarUrl(prompt.getUser().getAvatarUrl())
                .build();
    }
}
