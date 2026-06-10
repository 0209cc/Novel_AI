package com.cc.novel_ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建评论请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "Content is required")
    private String content;

    /**
     * 父评论ID（回复某条评论时使用）
     */
    private Long parentId;
}
