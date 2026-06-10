package com.cc.novel_ai.repository;

import com.cc.novel_ai.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 帖子数据访问接口
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 获取已发布的帖子（分页）
     */
    Page<Post> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    /**
     * 获取用户已发布的帖子（分页）
     */
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Integer status, Pageable pageable);

    /**
     * 根据ID和状态获取帖子
     */
    Optional<Post> findByIdAndStatus(Long id, Integer status);

    /**
     * 获取帖子详情（包含用户信息）
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.id = :id AND p.status = :status")
    Optional<Post> findByIdWithUser(@Param("id") Long id, @Param("status") Integer status);
}
