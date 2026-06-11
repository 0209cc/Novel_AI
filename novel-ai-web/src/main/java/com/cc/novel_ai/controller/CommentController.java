package com.cc.novel_ai.controller;

import com.cc.novel_ai.dto.request.CommentCreateRequest;
import com.cc.novel_ai.dto.response.ApiResponse;
import com.cc.novel_ai.dto.response.CommentResponse;
import com.cc.novel_ai.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request) {

        CommentResponse response = commentService.createComment(postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("已发送", response));
    }

    /**
     * 获取帖子的评论
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CommentResponse> response = commentService.getCommentsByPostId(postId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取评论的回复
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getReplies(@PathVariable Long commentId) {
        List<CommentResponse> response = commentService.getReplies(commentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("评论删除成功"));
    }
}
