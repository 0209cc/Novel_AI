package com.cc.novel_ai.service;

import com.cc.novel_ai.dto.mapper.PostMapper;
import com.cc.novel_ai.dto.request.PostCreateRequest;
import com.cc.novel_ai.dto.request.PostUpdateRequest;
import com.cc.novel_ai.dto.response.PageResponse;
import com.cc.novel_ai.dto.response.PostDetailResponse;
import com.cc.novel_ai.dto.response.PostResponse;
import com.cc.novel_ai.entity.Post;
import com.cc.novel_ai.entity.PostImage;
import com.cc.novel_ai.entity.User;
import com.cc.novel_ai.exception.BadRequestException;
import com.cc.novel_ai.exception.ResourceNotFoundException;
import com.cc.novel_ai.repository.CommentRepository;
import com.cc.novel_ai.repository.PostImageRepository;
import com.cc.novel_ai.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    /**
     * 创建帖子
     */
    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request, MultipartFile[] files) {
        User currentUser = userService.getCurrentUser();

        // 创建帖子
        Post post = Post.builder()
                .user(currentUser)
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(0)
                .status(1)
                .build();

        post = postRepository.save(post);

        // 上传图片
        if (files != null && files.length > 0) {
            List<PostImage> images = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(files[i]);
                    PostImage image = PostImage.builder()
                            .post(post)
                            .imageUrl(imageUrl)
                            .sortOrder(i)
                            .build();
                    images.add(image);
                }
            }
            postImageRepository.saveAll(images);
            post.setImages(images);
        }

        log.info("Post created successfully: id={}, user={}", post.getId(), currentUser.getUsername());

        return PostMapper.toDetailResponse(post, post.getImages(), 0);
    }

    /**
     * 获取帖子列表（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getPosts(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = postRepository.findByStatusOrderByCreatedAtDesc(1, pageable);

        List<PostResponse> posts = postPage.getContent().stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), 1);
                    return PostMapper.toResponse(post, commentCount);
                })
                .collect(Collectors.toList());

        return PageResponse.<PostResponse>builder()
                .content(posts)
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .build();
    }

    /**
     * 获取帖子详情
     */
    @Transactional
    public PostDetailResponse getPostById(Long id) {
        Post post = postRepository.findByIdWithUser(id, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        // 获取图片
        List<PostImage> images = postImageRepository.findByPostIdOrderBySortOrder(post.getId());

        // 获取评论数
        long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), 1);

        return PostMapper.toDetailResponse(post, images, commentCount);
    }

    /**
     * 更新帖子
     */
    @Transactional
    public PostDetailResponse updatePost(Long id, PostUpdateRequest request, MultipartFile[] files) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        // 验证权限
        User currentUser = userService.getCurrentUser();
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only edit your own posts");
        }

        // 更新帖子内容
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        post = postRepository.save(post);

        // 处理图片
        List<PostImage> existingImages = postImageRepository.findByPostIdOrderBySortOrder(post.getId());

        // 删除不在保留列表中的图片
        if (request.getExistingImageIds() != null) {
            List<PostImage> imagesToDelete = existingImages.stream()
                    .filter(img -> !request.getExistingImageIds().contains(img.getId()))
                    .collect(Collectors.toList());

            for (PostImage image : imagesToDelete) {
                fileStorageService.deleteFile(image.getImageUrl());
                postImageRepository.delete(image);
            }
        }

        // 添加新图片
        if (files != null && files.length > 0) {
            List<PostImage> newImages = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(files[i]);
                    PostImage image = PostImage.builder()
                            .post(post)
                            .imageUrl(imageUrl)
                            .sortOrder(i)
                            .build();
                    newImages.add(image);
                }
            }
            postImageRepository.saveAll(newImages);
        }

        // 重新获取图片列表
        List<PostImage> allImages = postImageRepository.findByPostIdOrderBySortOrder(post.getId());

        log.info("Post updated successfully: id={}, user={}", post.getId(), currentUser.getUsername());

        long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), 1);
        return PostMapper.toDetailResponse(post, allImages, commentCount);
    }

    /**
     * 删除帖子（软删除）
     */
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        // 验证权限
        User currentUser = userService.getCurrentUser();
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own posts");
        }

        // 软删除
        post.setStatus(0);
        postRepository.save(post);

        log.info("Post deleted successfully: id={}, user={}", post.getId(), currentUser.getUsername());
    }

    /**
     * 获取用户的帖子（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getUserPosts(Long userId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, 1, pageable);

        List<PostResponse> posts = postPage.getContent().stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPostIdAndStatus(post.getId(), 1);
                    return PostMapper.toResponse(post, commentCount);
                })
                .collect(Collectors.toList());

        return PageResponse.<PostResponse>builder()
                .content(posts)
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .build();
    }
}
