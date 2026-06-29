-- ============================================
-- Novel_AI 论坛系统数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS novel_ai_forum
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE novel_ai_forum;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS post_images;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(50)  DEFAULT NULL,
    avatar_url  VARCHAR(500) DEFAULT NULL,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0=disabled, 1=active',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. 帖子表
-- ============================================
CREATE TABLE posts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    view_count  INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0=deleted, 1=published',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_created_at (created_at DESC),
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. 帖子图片表
-- ============================================
CREATE TABLE post_images (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT       NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post_images_post_id (post_id),
    CONSTRAINT fk_images_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. 提示词表
-- ============================================
CREATE TABLE prompts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT         DEFAULT NULL,
    content     TEXT         NOT NULL,
    tags        VARCHAR(500) DEFAULT NULL,
    view_count  INT          NOT NULL DEFAULT 0,
    like_count  INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0=deleted, 1=published',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prompts_user_id (user_id),
    INDEX idx_prompts_created_at (created_at DESC),
    CONSTRAINT fk_prompts_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. 评论表
-- ============================================
CREATE TABLE comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    parent_id   BIGINT       DEFAULT NULL COMMENT 'null=top-level reply, otherwise=reply to another comment',
    content     TEXT         NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0=deleted, 1=visible',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_comments_post_id (created_at DESC),
    INDEX idx_comments_user_id (user_id),
    INDEX idx_comments_parent_id (parent_id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 插入测试数据（可选）
-- ============================================

-- 注意：密码是 "password123" 的 BCrypt 加密
-- 可以使用在线工具生成: https://www.bcrypt-generator.com/
INSERT INTO users (username, email, password, nickname, status) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 1),
('testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户', 1);

-- 插入测试帖子
INSERT INTO posts (user_id, title, content, view_count, status) VALUES
(1, '欢迎来到 Novel_AI 论坛', '这是一个测试帖子，欢迎使用论坛功能！', 100, 1),
(2, '如何使用图片上传功能', '本论坛支持图片上传，每张图片最大 5MB。', 50, 1);

-- 插入测试评论
INSERT INTO comments (post_id, user_id, parent_id, content, status) VALUES
(1, 2, NULL, '第一个评论！', 1),
(1, 1, 1, '谢谢支持！', 1),
(2, 1, NULL, '图片功能很好用！', 1);

-- 插入测试图片（示例 URL）
INSERT INTO post_images (post_id, image_url, sort_order) VALUES
(1, '/files/images/2026/06/example1.jpg', 0),
(1, '/files/images/2026/06/example2.jpg', 1);

-- ============================================
-- 完成
-- ============================================
SELECT '数据库初始化完成！' AS message;
