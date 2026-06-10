package com.cc.novel_ai.dto.mapper;

import com.cc.novel_ai.dto.response.PostDetailResponse;
import com.cc.novel_ai.dto.response.PostImageResponse;
import com.cc.novel_ai.dto.response.PostResponse;
import com.cc.novel_ai.entity.Post;
import com.cc.novel_ai.entity.PostImage;
import com.cc.novel_ai.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子 DTO 映射器
 */
public class PostMapper {

    /**
     * 将 Post 实体转换为 PostResponse
     */
    public static PostResponse toResponse(Post post, long commentCount) {
        User author = post.getUser();
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(author.getId())
                .authorName(author.getNickname() != null ? author.getNickname() : author.getUsername())
                .authorAvatar(author.getAvatarUrl())
                .viewCount(post.getViewCount())
                .commentCount((int) commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * 将 Post 实体转换为 PostDetailResponse
     */
    public static PostDetailResponse toDetailResponse(Post post, List<PostImage> images, long commentCount) {
        PostResponse baseResponse = toResponse(post, commentCount);

        List<PostImageResponse> imageResponses = images.stream()
                .map(PostMapper::toImageResponse)
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .id(baseResponse.getId())
                .title(baseResponse.getTitle())
                .content(baseResponse.getContent())
                .authorId(baseResponse.getAuthorId())
                .authorName(baseResponse.getAuthorName())
                .authorAvatar(baseResponse.getAuthorAvatar())
                .viewCount(baseResponse.getViewCount())
                .commentCount(baseResponse.getCommentCount())
                .createdAt(baseResponse.getCreatedAt())
                .updatedAt(baseResponse.getUpdatedAt())
                .images(imageResponses)
                .build();
    }

    /**
     * 将 PostImage 实体转换为 PostImageResponse
     */
    public static PostImageResponse toImageResponse(PostImage image) {
        return PostImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .sortOrder(image.getSortOrder())
                .build();
    }
}
