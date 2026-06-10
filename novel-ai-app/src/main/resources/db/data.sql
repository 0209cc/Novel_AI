-- ============================================
-- Novel_AI 论坛系统初始数据
-- ============================================

USE novel_ai_forum;

-- ============================================
-- 插入测试用户
-- ============================================

-- 密码说明：
-- 以下密码都是 "password123" 的 BCrypt 加密
-- 你可以使用在线工具生成新密码的 BCrypt 值:
-- https://www.bcrypt-generator.com/
-- 或者使用 Java 代码:
-- new BCryptPasswordEncoder().encode("your_password")

INSERT INTO users (username, email, password, nickname, avatar_url, status) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', NULL, 1),
('testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户', NULL, 1),
('zhangsan', 'zhangsan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张三', NULL, 1);

-- ============================================
-- 插入测试帖子
-- ============================================

INSERT INTO posts (user_id, title, content, view_count, status) VALUES
(1, '欢迎来到 Novel_AI 论坛', '这是一个测试帖子，欢迎使用论坛功能！\n\n本论坛支持：\n1. 文本发帖\n2. 图片上传\n3. 评论和回复\n\n快来体验吧！', 156, 1),
(2, '如何使用图片上传功能', '本论坛支持图片上传，每张图片最大 5MB。\n\n支持的图片格式：\n- JPEG\n- PNG\n- GIF\n- WEBP\n\n上传方法：\n1. 点击发帖按钮\n2. 填写标题和内容\n3. 选择要上传的图片\n4. 点击发布', 89, 1),
(1, '论坛功能介绍', '本论坛具有以下功能：\n\n## 用户功能\n- 用户注册\n- 用户登录\n- 个人资料查看\n\n## 帖子功能\n- 发布帖子\n- 编辑帖子\n- 删除帖子\n- 查看帖子详情\n- 浏览量统计\n\n## 评论功能\n- 发表评论\n- 回复评论\n- 删除评论\n\n## 图片功能\n- 上传多张图片\n- 图片预览\n- 图片访问', 234, 1),
(2, '测试帖子 - 图片展示', '这是一个展示图片功能的测试帖子。\n\n帖子可以包含多张图片，图片会按照上传顺序显示。', 45, 1);

-- ============================================
-- 插入测试评论
-- ============================================

-- 帖子 1 的评论
INSERT INTO comments (post_id, user_id, parent_id, content, status) VALUES
(1, 2, NULL, '第一个评论！论坛很棒！', 1),
(1, 1, 1, '谢谢支持！我们会继续改进的。', 1),
(1, 3, 1, '确实不错，期待更多功能！', 1),
(1, 3, NULL, '图片上传功能什么时候上线？', 1);

-- 帖子 2 的评论
INSERT INTO comments (post_id, user_id, parent_id, content, status) VALUES
(2, 1, NULL, '图片功能很好用！', 1),
(2, 3, 5, '同意，上传速度也很快。', 1);

-- 帖子 3 的评论
INSERT INTO comments (post_id, user_id, parent_id, content, status) VALUES
(3, 2, NULL, '功能介绍很详细，感谢分享！', 1),
(3, 1, 7, '不客气，有问题随时提问。', 1);

-- ============================================
-- 插入测试图片
-- ============================================

-- 注意：这些是示例 URL，实际使用时需要替换为真实的图片路径
INSERT INTO post_images (post_id, image_url, sort_order) VALUES
(1, '/files/images/2026/06/example1.jpg', 0),
(1, '/files/images/2026/06/example2.jpg', 1),
(2, '/files/images/2026/06/upload_guide.jpg', 0),
(3, '/files/images/2026/06/feature1.jpg', 0),
(3, '/files/images/2026/06/feature2.jpg', 1),
(3, '/files/images/2026/06/feature3.jpg', 2),
(4, '/files/images/2026/06/image_demo.jpg', 0);

-- ============================================
-- 完成
-- ============================================
SELECT '初始数据插入完成！' AS message;
SELECT COUNT(*) AS '用户数量' FROM users;
SELECT COUNT(*) AS '帖子数量' FROM posts;
SELECT COUNT(*) AS '评论数量' FROM comments;
SELECT COUNT(*) AS '图片数量' FROM post_images;
