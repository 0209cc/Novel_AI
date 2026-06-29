package com.cc.novel_ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建提示词请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    @NotBlank(message = "提示词内容不能为空")
    private String content;

    @Size(max = 500, message = "标签长度不能超过500个字符")
    private String tags;
}
