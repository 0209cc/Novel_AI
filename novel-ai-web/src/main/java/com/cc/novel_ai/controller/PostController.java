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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 帖子控制器
 */
@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 创建帖子（JSON，无文件）
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @Valid @RequestBody PostCreateRequest request) {

        PostDetailResponse response = postService.createPost(request, new MultipartFile[0]);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("帖子发布成功", response));
    }

    /**
     * 创建帖子（multipart，带文件）
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPostWithFiles(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
        PostCreateRequest request = mapper.readValue(dataJson, PostCreateRequest.class);

        MultipartFile[] fileArray = files != null ? files.toArray(new MultipartFile[0]) : new MultipartFile[0];
        PostDetailResponse response = postService.createPost(request, fileArray);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("帖子发布成功", response));
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
     * 更新帖子（JSON，无文件）
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request) {

        PostDetailResponse response = postService.updatePost(id, request, new MultipartFile[0]);
        return ResponseEntity.ok(ApiResponse.success("帖子更新成功", response));
    }

    /**
     * 更新帖子（multipart，带文件）
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePostWithFiles(
            @PathVariable Long id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
        PostUpdateRequest request = mapper.readValue(dataJson, PostUpdateRequest.class);

        MultipartFile[] fileArray = files != null ? files.toArray(new MultipartFile[0]) : new MultipartFile[0];
        PostDetailResponse response = postService.updatePost(id, request, fileArray);

        return ResponseEntity.ok(ApiResponse.success("帖子更新成功", response));
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("帖子删除成功"));
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
