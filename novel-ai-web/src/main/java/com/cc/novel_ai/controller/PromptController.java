package com.cc.novel_ai.controller;

import com.cc.novel_ai.dto.request.PromptCreateRequest;
import com.cc.novel_ai.dto.response.ApiResponse;
import com.cc.novel_ai.dto.response.PageResponse;
import com.cc.novel_ai.dto.response.PromptDetailResponse;
import com.cc.novel_ai.dto.response.PromptResponse;
import com.cc.novel_ai.service.PromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 提示词控制器
 */
@Slf4j
@RestController
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    /**
     * 创建提示词
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PromptDetailResponse>> createPrompt(
            @Valid @RequestBody PromptCreateRequest request) {

        PromptDetailResponse response = promptService.createPrompt(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("提示词发布成功", response));
    }

    /**
     * 获取提示词列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PromptResponse>>> getPrompts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<PromptResponse> response = promptService.getPrompts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取提示词详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptDetailResponse>> getPromptById(@PathVariable Long id) {
        PromptDetailResponse response = promptService.getPromptById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新提示词
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptDetailResponse>> updatePrompt(
            @PathVariable Long id,
            @Valid @RequestBody PromptCreateRequest request) {

        PromptDetailResponse response = promptService.updatePrompt(id, request);
        return ResponseEntity.ok(ApiResponse.success("提示词更新成功", response));
    }

    /**
     * 删除提示词
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePrompt(@PathVariable Long id) {
        promptService.deletePrompt(id);
        return ResponseEntity.ok(ApiResponse.success("提示词删除成功"));
    }

    /**
     * 获取用户提示词
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PromptResponse>>> getUserPrompts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<PromptResponse> response = promptService.getUserPrompts(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
