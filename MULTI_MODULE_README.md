# Novel_AI 多模块项目

## 项目结构

本项目已重构为 6 模块 Maven 多模块项目：

```
Novel_AI/
├── pom.xml                          # 父 POM（聚合器）
│
├── novel-ai-common/                 # 通用基础模块
│   ├── pom.xml
│   └── src/main/java/com/cc/novel_ai/
│       ├── dto/request/             # 请求 DTO（5 个）
│       ├── dto/response/            # 响应 DTO（8 个）
│       └── exception/               # 异常类（4 个）
│
├── novel-ai-domain/                 # 领域模型模块
│   ├── pom.xml
│   └── src/main/java/com/cc/novel_ai/
│       ├── entity/                  # 实体类（4 个）
│       └── repository/              # 数据访问层（4 个）
│
├── novel-ai-security/               # 安全认证模块
│   ├── pom.xml
│   └── src/main/java/com/cc/novel_ai/
│       ├── security/                # JWT 和 Security 实现（4 个）
│       └── config/SecurityConfig.java
│
├── novel-ai-service/                # 业务逻辑模块
│   ├── pom.xml
│   └── src/main/java/com/cc/novel_ai/
│       ├── service/                 # 业务服务（5 个）
│       ├── dto/mapper/              # DTO 映射器（2 个）
│       └── config/FileStorageConfig.java
│
├── novel-ai-web/                    # Web 层模块
│   ├── pom.xml
│   └── src/main/java/com/cc/novel_ai/
│       ├── controller/              # 控制器（5 个）
│       ├── config/WebConfig.java
│       └── exception/GlobalExceptionHandler.java
│
└── novel-ai-app/                    # 启动模块
    ├── pom.xml
    ├── src/main/java/com/cc/novel_ai/
    │   └── NovelAiApplication.java
    ├── src/main/resources/
    │   └── application.properties
    └── src/test/java/com/cc/novel_ai/
        └── NovelAiApplicationTests.java
```

## 模块依赖关系

```
common  (validation, jackson, lombok)
  |
  v
domain  (common + JPA)
  |
  v
security  (common + domain + spring-security + jjwt)
  |
  v
service  (common + domain + security + webmvc)
  |
  v
web     (common + service + security + webmvc)
  |
  v
app     (web + mysql-driver)
```

## 各模块职责

### novel-ai-common
**职责**: 通用基础，包含所有模块共享的 DTO 和异常类

**包含**:
- 请求 DTO（RegisterRequest, LoginRequest, PostCreateRequest, PostUpdateRequest, CommentCreateRequest）
- 响应 DTO（ApiResponse, PageResponse, AuthResponse, PostResponse, PostDetailResponse, PostImageResponse, CommentResponse, UserProfileResponse）
- 异常类（ResourceNotFoundException, BadRequestException, UnauthorizedException, FileStorageException）

**依赖**: spring-boot-starter-validation, jackson-annotations, lombok

### novel-ai-domain
**职责**: 领域模型，包含实体类和数据访问层

**包含**:
- 实体类（User, Post, PostImage, Comment）
- Repository 接口（UserRepository, PostRepository, PostImageRepository, CommentRepository）

**依赖**: novel-ai-common, spring-boot-starter-data-jpa

### novel-ai-security
**职责**: 安全认证，实现 JWT 认证和 Spring Security 配置

**包含**:
- UserDetailsImpl, CustomUserDetailsService
- JwtTokenProvider, JwtAuthenticationFilter
- SecurityConfig

**依赖**: novel-ai-common, novel-ai-domain, spring-boot-starter-security, jjwt, spring-boot-starter-webmvc

### novel-ai-service
**职责**: 业务逻辑，实现核心业务功能

**包含**:
- AuthService, UserService, PostService, CommentService, FileStorageService
- PostMapper, CommentMapper
- FileStorageConfig

**依赖**: novel-ai-common, novel-ai-domain, novel-ai-security, spring-boot-starter-webmvc

### novel-ai-web
**职责**: Web 层，处理 HTTP 请求和响应

**包含**:
- AuthController, UserController, PostController, CommentController, FileController
- WebConfig, GlobalExceptionHandler

**依赖**: novel-ai-common, novel-ai-service, novel-ai-security, spring-boot-starter-webmvc

### novel-ai-app
**职责**: 应用启动，打包为可执行 JAR

**包含**:
- NovelAiApplication（启动类）
- application.properties（配置文件）
- NovelAiApplicationTests（测试类）

**依赖**: novel-ai-web, mysql-connector-j, spring-boot-starter-webmvc-test

## 构建命令

```bash
# 编译所有模块
mvn clean compile

# 打包所有模块
mvn clean package -DskipTests

# 启动应用（从 app 模块）
mvn spring-boot:run -pl novel-ai-app

# 打包可执行 JAR
mvn clean package -DskipTests
# JAR 文件位于: novel-ai-app/target/novel-ai-app-0.0.1-SNAPSHOT.jar

# 运行 JAR
java -jar novel-ai-app/target/novel-ai-app-0.0.1-SNAPSHOT.jar
```

## 开发指南

### 添加新功能
1. **新实体**: 在 `novel-ai-domain` 中添加实体类和 Repository
2. **新 DTO**: 在 `novel-ai-common` 中添加请求/响应 DTO
3. **新服务**: 在 `novel-ai-service` 中添加业务逻辑
4. **新 API**: 在 `novel-ai-web` 中添加 Controller

### 依赖管理
- 所有模块版本由父 POM 统一管理
- 模块间依赖使用 `${project.version}` 确保版本一致
- `spring-boot-maven-plugin` 仅在 `novel-ai-app` 模块中配置

### 配置管理
- 所有配置集中在 `novel-ai-app/src/main/resources/application.properties`
- 其他模块通过 `@Value` 或 `@ConfigurationProperties` 注入配置

## 包名说明

所有模块保持相同的包名 `com.cc.novel_ai.*`，这是为了：
1. **零代码修改**: 迁移时无需修改任何 import 语句
2. **组件扫描**: Spring Boot 的 `@SpringBootApplication` 可以扫描所有模块
3. **兼容性**: 保持与单模块项目的 API 兼容性

拆分包（如 `com.cc.novel_ai.config` 跨 3 个模块）在非 JPMS 环境下完全正常。

## 注意事项

1. **Lombok 配置**: 每个使用 Lombok 的模块都需要在 pom.xml 中配置 `maven-compiler-plugin` 的 `annotationProcessorPaths`
2. **插件管理**: `spring-boot-maven-plugin` 仅在 `novel-ai-app` 模块中配置，避免其他模块生成 fat JAR
3. **配置文件**: `application.properties` 仅在 `novel-ai-app` 模块中，其他模块通过依赖传递获取
4. **测试**: 测试类仅在 `novel-ai-app` 模块中，可以访问所有依赖
5. **上下文路径**: 所有 API 路由都以 `/novel` 开头（已配置 `server.servlet.context-path=/novel`）

## API 路由示例

所有 API 端点都以 `/novel` 作为前缀：

### 认证 API
- 注册: `POST /novel/auth/register`
- 登录: `POST /novel/auth/login`

### 帖子 API
- 创建帖子: `POST /novel/posts`
- 获取帖子列表: `GET /novel/posts`
- 获取帖子详情: `GET /novel/posts/{id}`
- 更新帖子: `PUT /novel/posts/{id}`
- 删除帖子: `DELETE /novel/posts/{id}`
- 获取用户帖子: `GET /novel/posts/user/{userId}`

### 评论 API
- 创建评论: `POST /novel/posts/{postId}/comments`
- 获取帖子评论: `GET /novel/posts/{postId}/comments`
- 获取评论回复: `GET /novel/comments/{commentId}/replies`
- 删除评论: `DELETE /novel/comments/{id}`

### 用户 API
- 获取当前用户: `GET /novel/users/me`

### 文件 API
- 访问图片: `GET /novel/files/images/{filename}`

## 迁移历史

本项目从单模块结构迁移到多模块结构，迁移过程中：
- ✅ 所有 43 个 Java 源文件保持不变
- ✅ 零 import 语句修改
- ✅ 零包名修改
- ✅ 功能完全保持一致
- ✅ 编译和打包验证通过

## License

MIT
