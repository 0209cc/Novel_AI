package com.cc.novel_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 帖子图片响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageResponse {

    private Long id;
    private String imageUrl;
    private int sortOrder;
}
