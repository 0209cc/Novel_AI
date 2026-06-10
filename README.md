# Novel_AI 论坛系统

基于 Spring Boot 4.0.6 的论坛发帖和评论系统，支持用户认证、图片上传和评论功能。

## 技术栈

- **后端**: Spring Boot 4.0.6 + Java 17
- **数据库**: MySQL 8.x + Spring Data JPA
- **认证**: Spring Security + JWT
- **构建工具**: Maven

## 项目结构

```
com.cc.novel_ai
├── config/
│   ├── SecurityConfig.java          # Spring Security 配置
│   ├── WebConfig.java               # Web 配置（CORS、资源映射）
│   └── FileStorageConfig.java       # 文件存储配置
├── security/
│   ├── JwtTokenProvider.java        # JWT Token 工具类
│   ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│   ├── CustomUserDetailsService.java # 用户详情服务
│   └── UserDetailsImpl.java         # 用户详情实现
├── entity/
│   ├── User.java                    # 用户实体
│   ├── Post.java                    # 帖子实体
│   ├── PostImage.java               # 帖子图片实体
│   └── Comment.java                 # 评论实体
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── PostImageRepository.java
│   └── CommentRepository.java
├── dto/
│   ├── request/                     # 请求 DTO
│   ├── response/                    # 响应 DTO
│   └── mapper/                      # DTO 映射器
├── service/
│   ├── AuthService.java             # 认证服务
│   ├── UserService.java             # 用户服务
│   ├── PostService.java             # 帖子服务
│   ├── CommentService.java          # 评论服务
│   └── FileStorageService.java      # 文件存储服务
├── controller/
│   ├── AuthController.java          # 认证 API
│   ├── PostController.java          # 帖子 API
│   ├── CommentController.java       # 评论 API
│   ├── UserController.java          # 用户 API
│   └── FileController.java          # 文件访问 API
└── exception/
    ├── GlobalExceptionHandler.java  # 全局异常处理
    ├── ResourceNotFoundException.java
    ├── BadRequestException.java
    ├── UnauthorizedException.java
    └── FileStorageException.java
```

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.x

### 2. 数据库准备

在 MySQL 中创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS novel_ai_forum
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 3. 修改配置

编辑 `src/main/resources/application.properties`：

```properties
# 数据库配置（修改为你自己的密码）
spring.datasource.password=your_password

# JWT 密钥（生产环境请修改为安全的随机字符串）
jwt.secret=your-256-bit-secret-key
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 文档

### 认证 API

#### 注册用户
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "nickname": "Test User"
}

Response:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "testuser"
  }
}
```

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "testuser"
  }
}
```

### 帖子 API

#### 创建帖子（需要认证）
```http
POST /api/posts
Content-Type: multipart/form-data

data: {"title": "First Post", "content": "Hello World!"}
files: [image1.jpg, image2.png]

Response:
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "title": "First Post",
    "content": "Hello World!",
    "authorId": 1,
    "authorName": "Test User",
    "viewCount": 0,
    "commentCount": 0,
    "createdAt": "2026-06-10T10:00:00",
    "images": [
      {
        "id": 1,
        "imageUrl": "/files/images/2026/06/uuid1.jpg",
        "sortOrder": 0
      }
    ]
  }
}
```

#### 获取帖子列表
```http
GET /api/posts?page=0&size=20

Response:
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

#### 获取帖子详情
```http
GET /api/posts/1

Response:
{
  "success": true,
  "data": {
    "id": 1,
    "title": "First Post",
    "content": "Hello World!",
    "authorId": 1,
    "authorName": "Test User",
    "viewCount": 1,
    "commentCount": 5,
    "images": [...]
  }
}
```

#### 更新帖子（需要认证，仅作者）
```http
PUT /api/posts/1
Content-Type: multipart/form-data

data: {"title": "Updated Title", "content": "Updated content"}
files: [new_image.jpg]

Response:
{
  "success": true,
  "message": "Post updated successfully",
  "data": {...}
}
```

#### 删除帖子（需要认证，仅作者）
```http
DELETE /api/posts/1

Response:
{
  "success": true,
  "message": "Post deleted successfully"
}
```

### 评论 API

#### 创建评论（需要认证）
```http
POST /api/posts/1/comments
Content-Type: application/json

{
  "content": "Great post!",
  "parentId": null  // 回复某条评论时填写父评论ID
}

Response:
{
  "success": true,
  "message": "Comment created successfully",
  "data": {
    "id": 1,
    "content": "Great post!",
    "authorId": 1,
    "authorName": "Test User",
    "postId": 1,
    "parentId": null,
    "replyCount": 0,
    "createdAt": "2026-06-10T10:30:00"
  }
}
```

#### 获取帖子评论
```http
GET /api/posts/1/comments?page=0&size=20

Response:
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3
  }
}
```

#### 获取评论回复
```http
GET /api/comments/1/replies

Response:
{
  "success": true,
  "data": [
    {
      "id": 2,
      "content": "Thanks!",
      "authorId": 2,
      "authorName": "Another User",
      "parentId": 1,
      "replyCount": 0
    }
  ]
}
```

#### 删除评论（需要认证，仅作者）
```http
DELETE /api/comments/1

Response:
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

### 用户 API

#### 获取当前用户信息（需要认证）
```http
GET /api/users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Response:
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "Test User",
    "avatarUrl": null,
    "createdAt": "2026-06-10T09:00:00"
  }
}
```

### 文件 API

#### 访问上传的图片
```http
GET /files/images/2026/06/uuid1.jpg

Response: 图片二进制流
```

## 功能特性

### ✅ 已实现

1. **用户认证**
   - 用户注册（用户名、邮箱唯一性检查）
   - 用户登录（JWT Token 认证）
   - 密码加密存储（BCrypt）

2. **帖子管理**
   - 创建帖子（支持文本 + 多张图片）
   - 编辑帖子（仅作者可编辑）
   - 删除帖子（软删除，仅作者可删除）
   - 帖子列表（分页查询）
   - 帖子详情（自动增加浏览量）

3. **图片上传**
   - 本地文件存储
   - 按日期组织目录（yyyy/MM）
   - 文件类型验证（JPEG、PNG、GIF、WEBP）
   - 文件大小限制（最大 5MB）
   - 路径遍历攻击防护

4. **评论系统**
   - 一级评论
   - 评论回复（支持一级嵌套）
   - 评论删除（软删除）

5. **安全特性**
   - JWT Token 认证
   - 密码加密
   - 路径遍历攻击防护
   - CORS 配置

6. **API 设计**
   - RESTful 风格
   - 统一响应格式
   - 分页支持
   - 参数验证

## 配置说明

### application.properties

```properties
# 数据库
spring.datasource.url=jdbc:mysql://localhost:3306/novel_ai_forum?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update  # 自动更新数据库结构
spring.jpa.show-sql=true

# 文件上传
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=20MB
app.upload.path=./uploads/images/
app.upload.url-prefix=/files/images/

# JWT
jwt.secret=your-256-bit-secret-key
jwt.expiration-ms=86400000  # 24小时
```

## 测试建议

1. 使用 Postman 或 curl 测试 API
2. 先注册用户，获取 JWT Token
3. 在请求头中添加 `Authorization: Bearer <token>`
4. 测试帖子创建、编辑、删除
5. 测试评论创建、查看、删除
6. 测试图片上传和访问

## 注意事项

1. **JWT 密钥**: 生产环境请修改为安全的随机字符串
2. **数据库密码**: 请修改为实际的 MySQL 密码
3. **上传目录**: 确保应用有写入权限
4. **CORS**: 根据实际前端地址修改 WebConfig 中的 allowedOrigins

## License

MIT
