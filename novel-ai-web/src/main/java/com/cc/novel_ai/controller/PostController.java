package com.cc.novel_ai.controller;

import com.cc.novel_ai.dto.request.PostCreateRequest;
import com.cc.novel_ai.dto.request.PostUpdateRequest;
import com.cc.novel_ai.dto.response.ApiResponse;
import com.cc.novel_ai.dto.response.PageResponse;
import com.cc.novel_ai.dto.response.PostDetailResponse;
import com.cc.novel_ai.dto.response.PostResponse;
import com.cc.novel_ai.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 创建帖子
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @RequestPart("data") @Valid PostCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        MultipartFile[] fileArray = files != null ? files.toArray(new MultipartFile[0]) : new MultipartFile[0];
        PostDetailResponse response = postService.createPost(request, fileArray);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", response));
    }

    /**
     * 获取帖子列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<PostResponse> response = postService.getPosts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostById(@PathVariable Long id) {
        PostDetailResponse response = postService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新帖子
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long id,
            @RequestPart("data") @Valid PostUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        MultipartFile[] fileArray = files != null ? files.toArray(new MultipartFile[0]) : new MultipartFile[0];
        PostDetailResponse response = postService.updatePost(id, request, fileArray);

        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", response));
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully"));
    }

    /**
     * 获取用户帖子
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<PostResponse> response = postService.getUserPosts(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
