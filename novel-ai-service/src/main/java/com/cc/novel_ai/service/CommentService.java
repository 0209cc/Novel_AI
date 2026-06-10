package com.cc.novel_ai.service;

import com.cc.novel_ai.dto.mapper.CommentMapper;
import com.cc.novel_ai.dto.request.CommentCreateRequest;
import com.cc.novel_ai.dto.response.CommentResponse;
import com.cc.novel_ai.entity.Comment;
import com.cc.novel_ai.entity.Post;
import com.cc.novel_ai.entity.User;
import com.cc.novel_ai.exception.BadRequestException;
import com.cc.novel_ai.exception.ResourceNotFoundException;
import com.cc.novel_ai.repository.CommentRepository;
import com.cc.novel_ai.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    /**
     * 创建评论
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request) {
        User currentUser = userService.getCurrentUser();

        // 验证帖子存在
        Post post = postRepository.findByIdAndStatus(postId, 1)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        // 验证父评论（如果有）
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findByIdAndStatus(request.getParentId(), 1);
            if (parent == null) {
                throw new ResourceNotFoundException("Parent comment", request.getParentId());
            }
            // 验证父评论是否属于同一帖子
            if (!parent.getPost().getId().equals(postId)) {
                throw new BadRequestException("父评论不属于该帖子");
            }
        }

        // 创建评论
        Comment comment = Comment.builder()
                .post(post)
                .user(currentUser)
                .parent(parent)
                .content(request.getContent())
                .status(1)
                .build();

        comment = commentRepository.save(comment);

        log.info("Comment created successfully: postId={}, userId={}", postId, currentUser.getUsername());

        return CommentMapper.toResponse(comment, 0);
    }

    /**
     * 获取帖子的评论（分页，顶级评论）
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPostId(Long postId, int page, int size) {
        // 验证帖子存在
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post", postId);
        }

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Comment> commentPage = commentRepository.findByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtDesc(
                postId, 1, pageable);

        return commentPage.map(comment -> {
            long replyCount = commentRepository.countByPostIdAndStatus(postId, 1);
            return CommentMapper.toResponse(comment, replyCount);
        });
    }

    /**
     * 获取评论的回复列表
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(Long commentId) {
        // 验证评论存在
        Comment parentComment = commentRepository.findByIdAndStatus(commentId, 1);
        if (parentComment == null) {
            throw new ResourceNotFoundException("Comment", commentId);
        }

        List<Comment> replies = commentRepository.findByParentIdAndStatusOrderByCreatedAtAsc(commentId, 1);

        return replies.stream()
                .map(reply -> {
                    long replyCount = commentRepository.countByPostIdAndStatus(reply.getId(), 1);
                    return CommentMapper.toResponse(reply, replyCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findByIdAndStatus(id, 1);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment", id);
        }

        // 验证权限
        User currentUser = userService.getCurrentUser();
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("只能删除自己的评论");
        }

        // 软删除
        comment.setStatus(0);
        commentRepository.save(comment);

        log.info("Comment deleted successfully: id={}, user={}", id, currentUser.getUsername());
    }
}
