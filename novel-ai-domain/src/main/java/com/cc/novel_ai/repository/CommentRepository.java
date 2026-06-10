package com.cc.novel_ai.repository;

import com.cc.novel_ai.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问接口
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 获取帖子的顶级评论（分页）
     */
    Page<Comment> findByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtDesc(
            Long postId, Integer parentId, Integer status, Pageable pageable);

    /**
     * 获取评论的回复列表
     */
    List<Comment> findByParentIdAndStatusOrderByCreatedAtAsc(Long parentId, Integer status);

    /**
     * 统计帖子的有效评论数
     */
    long countByPostIdAndStatus(Long postId, Integer status);

    /**
     * 根据ID和状态获取评论
     */
    Comment findByIdAndStatus(Long id, Integer status);
}
