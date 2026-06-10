package com.cc.novel_ai.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新帖子请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    private String content;

    /**
     * 要保留的现有图片ID列表
     */
    private List<Long> existingImageIds;
}
