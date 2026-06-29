package com.cc.novel_ai.repository;

import com.cc.novel_ai.entity.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 提示词数据访问接口
 */
@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {

    /**
     * 获取已发布的提示词（分页）
     */
    Page<Prompt> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    /**
     * 获取用户已发布的提示词（分页）
     */
    Page<Prompt> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Integer status, Pageable pageable);

    /**
     * 根据ID和状态获取提示词
     */
    Optional<Prompt> findByIdAndStatus(Long id, Integer status);

    /**
     * 获取提示词详情（包含用户信息）
     */
    @Query("SELECT p FROM Prompt p JOIN FETCH p.user WHERE p.id = :id AND p.status = :status")
    Optional<Prompt> findByIdWithUser(@Param("id") Long id, @Param("status") Integer status);
}
