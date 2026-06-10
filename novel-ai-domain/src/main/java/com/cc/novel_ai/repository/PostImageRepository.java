package com.cc.novel_ai.repository;

import com.cc.novel_ai.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子图片数据访问接口
 */
@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    /**
     * 根据帖子ID获取图片列表
     */
    List<PostImage> findByPostIdOrderBySortOrder(Long postId);

    /**
     * 根据帖子ID删除所有图片
     */
    void deleteByPostId(Long postId);
}
