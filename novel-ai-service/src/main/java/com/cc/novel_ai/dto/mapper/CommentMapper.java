package com.cc.novel_ai.dto.mapper;

import com.cc.novel_ai.dto.response.CommentResponse;
import com.cc.novel_ai.entity.Comment;
import com.cc.novel_ai.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论 DTO 映射器
 */
public class CommentMapper {

    /**
     * 将 Comment 实体转换为 CommentResponse
     */
    public static CommentResponse toResponse(Comment comment, long replyCount) {
        User author = comment.getUser();
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(author.getId())
                .authorName(author.getNickname() != null ? author.getNickname() : author.getUsername())
                .authorAvatar(author.getAvatarUrl())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replyCount((int) replyCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * 将 Comment 实体列表转换为 CommentResponse 列表
     */
    public static List<CommentResponse> toResponseList(List<Comment> comments, long replyCount) {
        return comments.stream()
                .map(comment -> toResponse(comment, replyCount))
                .collect(Collectors.toList());
    }
}
