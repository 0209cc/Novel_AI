# 数据库初始化说明

## 文件说明

本目录包含数据库初始化脚本：

- `schema.sql` - 数据库表结构创建脚本
- `data.sql` - 初始测试数据插入脚本

## 使用方法

### 方法 1: 使用 MySQL 命令行

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行建表脚本
source /path/to/novel-ai-app/src/main/resources/db/schema.sql

# 3. 执行数据初始化脚本（可选）
source /path/to/novel-ai-app/src/main/resources/db/data.sql
```

### 方法 2: 使用 MySQL Workbench 或其他 GUI 工具

1. 打开 MySQL Workbench
2. 连接到 MySQL 服务器
3. 打开 `schema.sql` 文件并执行
4. 打开 `data.sql` 文件并执行（可选）

### 方法 3: 使用 Spring Boot 自动初始化

在 `application.properties` 中添加以下配置：

```properties
# 启用数据库初始化
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:db/schema.sql
spring.sql.init.data-locations=classpath:db/data.sql

# 或者只在首次运行时初始化
spring.sql.init.mode=embedded
```

**注意**: 使用自动初始化时，脚本会在每次应用启动时执行。建议在生产环境中使用 `mode=never`。

## 数据库结构

### users 表（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| email | VARCHAR(100) | 邮箱，唯一 |
| password | VARCHAR(255) | 密码（BCrypt 加密）|
| nickname | VARCHAR(50) | 昵称 |
| avatar_url | VARCHAR(500) | 头像 URL |
| status | TINYINT | 状态：0=禁用，1=启用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### posts 表（帖子表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户 ID（外键）|
| title | VARCHAR(200) | 帖子标题 |
| content | TEXT | 帖子内容 |
| view_count | INT | 浏览量 |
| status | TINYINT | 状态：0=已删除，1=已发布 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### post_images 表（帖子图片表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| post_id | BIGINT | 帖子 ID（外键）|
| image_url | VARCHAR(500) | 图片 URL |
| sort_order | INT | 排序顺序 |
| created_at | DATETIME | 创建时间 |

### comments 表（评论表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| post_id | BIGINT | 帖子 ID（外键）|
| user_id | BIGINT | 用户 ID（外键）|
| parent_id | BIGINT | 父评论 ID（回复某条评论时使用）|
| content | TEXT | 评论内容 |
| status | TINYINT | 状态：0=已删除，1=可见 |
| created_at | DATETIME | 创建时间 |

## 测试数据说明

### 测试用户

| 用户名 | 密码 | 昵称 | 说明 |
|--------|------|------|------|
| admin | password123 | 管理员 | 管理员账户 |
| testuser | password123 | 测试用户 | 普通用户 |
| zhangsan | password123 | 张三 | 普通用户 |

**注意**: 所有测试用户的密码都是 `password123`

### 测试帖子

- 欢迎来到 Novel_AI 论坛
- 如何使用图片上传功能
- 论坛功能介绍
- 测试帖子 - 图片展示

### 测试评论

每个帖子都有一些测试评论，包括：
- 顶级评论
- 对评论的回复（一级嵌套）

## 密码加密

本系统使用 BCrypt 算法加密密码。如果你需要生成新密码的 BCrypt 值，可以使用以下方法：

### 方法 1: 在线工具
访问 https://www.bcrypt-generator.com/ 生成 BCrypt 值

### 方法 2: Java 代码
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encodedPassword = encoder.encode("your_password");
System.out.println(encodedPassword);
```

### 方法 3: 命令行工具
```bash
# 使用 htpasswd 命令（需要安装 apache2-utils）
htpasswd -bnBC 10 "" your_password | tr -d ':\n' | sed 's/$2y/$2a/'
```

## 常见问题

### 1. 外键约束错误
如果遇到外键约束错误，请确保按顺序执行脚本：
1. 先执行 `schema.sql` 创建表
2. 再执行 `data.sql` 插入数据

### 2. 字符集问题
确保 MySQL 使用 UTF-8 字符集：
```sql
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
```

### 3. 重复执行脚本
脚本使用了 `DROP TABLE IF EXISTS`，可以安全地重复执行。但请注意，这会删除所有现有数据。

### 4. 生产环境使用
在生产环境中：
- 不要使用 `data.sql` 中的测试数据
- 修改所有测试用户的密码
- 删除或修改测试帖子和评论
- 使用 `spring.sql.init.mode=never` 禁用自动初始化

## 数据库维护

### 备份数据库
```bash
mysqldump -u root -p novel_ai_forum > backup.sql
```

### 恢复数据库
```bash
mysql -u root -p novel_ai_forum < backup.sql
```

### 清空所有数据（保留表结构）
```sql
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE comments;
TRUNCATE TABLE post_images;
TRUNCATE TABLE posts;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;
```

## 联系方式

如有问题，请联系项目维护者。
