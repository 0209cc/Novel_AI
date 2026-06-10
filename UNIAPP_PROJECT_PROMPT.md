# Novel_AI UniApp 前端项目开发提示词

## 项目概述

基于 Spring Boot 后端 API 开发一个 UniApp 跨平台移动端应用，实现论坛发帖、评论、图片上传等功能。

## 技术栈要求

- **框架**: UniApp (Vue 3 + Composition API)
- **UI 组件库**: uView Plus 或 uni-ui
- **状态管理**: Pinia
- **网络请求**: uni.request (封装为统一请求工具)
- **图片处理**: uni.chooseImage, uni.previewImage
- **存储**: uni.setStorageSync / uni.getStorageSync

## 后端 API 基础信息

- **基础路径**: `http://localhost:8080/novel`
- **数据格式**: JSON
- **认证方式**: JWT Token (Bearer)
- **请求头**: `Authorization: Bearer {token}`

## API 接口文档

### 1. 认证模块

#### 1.1 用户注册
```
POST /auth/register
Content-Type: application/json

请求体:
{
  "username": "string (3-50字符，必填)",
  "email": "string (邮箱格式，必填)",
  "password": "string (6-100字符，必填)",
  "nickname": "string (可选)"
}

响应:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "string (JWT Token)",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "string"
  }
}
```

#### 1.2 用户登录
```
POST /auth/login
Content-Type: application/json

请求体:
{
  "username": "string (必填)",
  "password": "string (必填)"
}

响应:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "string (JWT Token)",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "string"
  }
}
```

### 2. 用户模块

#### 2.1 获取当前用户信息
```
GET /users/me
Authorization: Bearer {token}

响应:
{
  "success": true,
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "nickname": "string",
    "avatarUrl": "string (可为null)",
    "createdAt": "2026-06-10T10:00:00"
  }
}
```

### 3. 帖子模块

#### 3.1 创建帖子（需要登录）
```
POST /posts
Content-Type: multipart/form-data
Authorization: Bearer {token}

请求体:
- data: JSON字符串 {
    "title": "string (1-200字符，必填)",
    "content": "string (必填)"
  }
- files: File[] (可选，最多9张图片，每张最大5MB)

响应:
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "title": "string",
    "content": "string",
    "authorId": 1,
    "authorName": "string",
    "authorAvatar": "string",
    "viewCount": 0,
    "commentCount": 0,
    "createdAt": "2026-06-10T10:00:00",
    "updatedAt": "2026-06-10T10:00:00",
    "images": [
      {
        "id": 1,
        "imageUrl": "/files/images/2026/06/uuid.jpg",
        "sortOrder": 0
      }
    ]
  }
}
```

#### 3.2 获取帖子列表（分页）
```
GET /posts?page=0&size=20

响应:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "string",
        "content": "string",
        "authorId": 1,
        "authorName": "string",
        "authorAvatar": "string",
        "viewCount": 100,
        "commentCount": 5,
        "createdAt": "2026-06-10T10:00:00",
        "updatedAt": "2026-06-10T10:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

#### 3.3 获取帖子详情
```
GET /posts/{id}

响应:
{
  "success": true,
  "data": {
    "id": 1,
    "title": "string",
    "content": "string",
    "authorId": 1,
    "authorName": "string",
    "authorAvatar": "string",
    "viewCount": 101,
    "commentCount": 5,
    "createdAt": "2026-06-10T10:00:00",
    "updatedAt": "2026-06-10T10:00:00",
    "images": [
      {
        "id": 1,
        "imageUrl": "/files/images/2026/06/uuid.jpg",
        "sortOrder": 0
      }
    ]
  }
}
```

#### 3.4 更新帖子（需要登录，仅作者）
```
PUT /posts/{id}
Content-Type: multipart/form-data
Authorization: Bearer {token}

请求体:
- data: JSON字符串 {
    "title": "string (可选)",
    "content": "string (可选)",
    "existingImageIds": [1, 2] (要保留的图片ID列表，可选)
  }
- files: File[] (新上传的图片，可选)

响应:
{
  "success": true,
  "message": "Post updated successfully",
  "data": { ... }  // 同创建帖子响应
}
```

#### 3.5 删除帖子（需要登录，仅作者）
```
DELETE /posts/{id}
Authorization: Bearer {token}

响应:
{
  "success": true,
  "message": "Post deleted successfully"
}
```

#### 3.6 获取用户帖子（分页）
```
GET /posts/user/{userId}?page=0&size=20

响应: 同获取帖子列表
```

### 4. 评论模块

#### 4.1 创建评论（需要登录）
```
POST /posts/{postId}/comments
Content-Type: application/json
Authorization: Bearer {token}

请求体:
{
  "content": "string (必填)",
  "parentId": 1 (可选，回复某条评论时填写父评论ID)
}

响应:
{
  "success": true,
  "message": "Comment created successfully",
  "data": {
    "id": 1,
    "content": "string",
    "authorId": 1,
    "authorName": "string",
    "authorAvatar": "string",
    "postId": 1,
    "parentId": null,
    "replyCount": 0,
    "createdAt": "2026-06-10T10:30:00"
  }
}
```

#### 4.2 获取帖子评论（分页）
```
GET /posts/{postId}/comments?page=0&size=20

响应:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "content": "string",
        "authorId": 1,
        "authorName": "string",
        "authorAvatar": "string",
        "postId": 1,
        "parentId": null,
        "replyCount": 3,
        "createdAt": "2026-06-10T10:30:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3
  }
}
```

#### 4.3 获取评论回复
```
GET /comments/{commentId}/replies

响应:
{
  "success": true,
  "data": [
    {
      "id": 2,
      "content": "string",
      "authorId": 2,
      "authorName": "string",
      "authorAvatar": "string",
      "postId": 1,
      "parentId": 1,
      "replyCount": 0,
      "createdAt": "2026-06-10T10:35:00"
    }
  ]
}
```

#### 4.4 删除评论（需要登录，仅作者）
```
DELETE /comments/{id}
Authorization: Bearer {token}

响应:
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

### 5. 文件模块

#### 5.1 访问图片
```
GET /files/images/{filename}

响应: 图片二进制流
```

## 页面设计要求

### 1. 启动页/引导页
- 应用 Logo
- 简介文字
- 自动跳转到首页

### 2. 登录/注册页
- 登录表单：用户名、密码
- 注册表单：用户名、邮箱、密码、确认密码、昵称（可选）
- 登录/注册切换
- 表单验证
- 记住登录状态

### 3. 首页（帖子列表）
- 顶部导航栏：应用名称、搜索图标（可选）
- 帖子卡片列表（下拉刷新、上拉加载更多）
  - 用户头像、昵称
  - 帖子标题
  - 帖子内容预览（最多3行）
  - 图片预览（最多显示3张缩略图）
  - 浏览量、评论数、发布时间
- 底部 TabBar：首页、发布、我的
- 右下角浮动按钮：发布帖子

### 4. 帖子详情页
- 顶部导航栏：返回按钮、标题、更多操作（编辑/删除，仅作者可见）
- 用户信息：头像、昵称、发布时间
- 帖子内容（支持富文本/Markdown 渲染）
- 图片展示（可点击预览，支持左右滑动）
- 浏览量显示
- 底部评论区
  - 评论输入框
  - 评论列表（支持一级嵌套回复）
  - 每条评论显示：用户头像、昵称、内容、时间、回复按钮
- 底部固定：评论输入框、点赞按钮（可选）

### 5. 发布帖子页
- 顶部导航栏：返回按钮、标题"发布帖子"、发布按钮
- 表单内容：
  - 标题输入框（必填，最多200字符）
  - 内容输入框（必填，支持多行，富文本编辑器可选）
  - 图片上传区
    - 最多9张图片
    - 支持预览、删除、排序
    - 显示上传进度
- 发布前预览功能

### 6. 个人中心页
- 用户信息卡片
  - 头像（可点击更换）
  - 昵称
  - 用户名
  - 邮箱
- 我的帖子（跳转到帖子列表，只显示自己的帖子）
- 设置
  - 修改密码（可选）
  - 清除缓存
  - 关于我们
- 退出登录

### 7. 编辑帖子页
- 同发布帖子页，但预填充原有内容
- 支持删除已有图片
- 支持添加新图片

### 8. 图片预览页
- 全屏预览图片
- 支持左右滑动切换
- 支持缩放
- 保存到本地（可选）

## 功能需求

### 1. 用户认证
- 登录状态持久化（Token 存储）
- 自动登录（Token 有效期内）
- 登录过期处理（自动跳转登录页）
- 退出登录

### 2. 帖子功能
- 帖子列表展示（分页加载）
- 下拉刷新
- 上拉加载更多
- 帖子详情查看
- 发布帖子
- 编辑帖子（仅作者）
- 删除帖子（仅作者，二次确认）
- 浏览量统计

### 3. 图片功能
- 图片选择（从相册或拍照）
- 图片预览（全屏、缩放、左右滑动）
- 图片上传（显示进度）
- 图片删除
- 图片排序（长按拖动）

### 4. 评论功能
- 查看评论列表
- 发表评论
- 回复评论（一级嵌套）
- 删除评论（仅作者，二次确认）

### 5. 用户功能
- 查看个人信息
- 查看我的帖子
- 退出登录

## 代码规范

### 1. 项目结构
```
src/
├── api/                    # API 接口封装
│   ├── auth.js            # 认证相关接口
│   ├── user.js            # 用户相关接口
│   ├── post.js            # 帖子相关接口
│   └── comment.js         # 评论相关接口
├── components/             # 公共组件
│   ├── PostCard.vue       # 帖子卡片组件
│   ├── CommentItem.vue    # 评论项组件
│   ├── ImageUploader.vue  # 图片上传组件
│   └── EmptyState.vue     # 空状态组件
├── pages/                  # 页面
│   ├── index/             # 首页
│   ├── login/             # 登录页
│   ├── register/          # 注册页
│   ├── post/              # 帖子相关页面
│   │   ├── detail.vue     # 帖子详情
│   │   ├── create.vue     # 发布帖子
│   │   └── edit.vue       # 编辑帖子
│   ├── comment/           # 评论相关（可能嵌入帖子详情）
│   └── user/              # 用户相关页面
│       ├── profile.vue    # 个人中心
│       └── settings.vue   # 设置
├── store/                  # Pinia 状态管理
│   ├── user.js            # 用户状态
│   └── post.js            # 帖子状态（可选）
├── utils/                  # 工具函数
│   ├── request.js         # 网络请求封装
│   ├── auth.js            # 认证工具
│   ├── storage.js         # 本地存储工具
│   └── format.js          # 格式化工具（时间、数字等）
├── static/                 # 静态资源
│   ├── images/            # 图片资源
│   └── icons/             # 图标资源
├── App.vue                 # 根组件
├── main.js                 # 入口文件
├── manifest.json           # 应用配置
├── pages.json              # 页面路由配置
└── uni.scss                # 全局样式变量
```

### 2. 命名规范
- 文件名：kebab-case（如 `post-detail.vue`）
- 组件名：PascalCase（如 `PostCard.vue`）
- 变量名：camelCase（如 `userName`）
- 常量名：UPPER_SNAKE_CASE（如 `BASE_URL`）
- CSS 类名：kebab-case（如 `post-card`）

### 3. 代码风格
- 使用 Composition API（`<script setup>`）
- 使用 ESLint + Prettier 格式化代码
- 组件 props 使用类型定义
- 使用 TypeScript（可选，推荐）

## 网络请求封装示例

```javascript
// utils/request.js
const BASE_URL = 'http://localhost:8080/novel'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': options.contentType || 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // Token 过期，跳转登录页
          uni.removeStorageSync('token')
          uni.redirectTo({ url: '/pages/login/login' })
          reject(new Error('未授权'))
        } else {
          reject(new Error(res.data.message || '请求失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export default request
```

## 状态管理示例

```javascript
// store/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.userInfo?.id
  },
  
  actions: {
    setToken(token) {
      this.token = token
      uni.setStorageSync('token', token)
    },
    
    setUserInfo(info) {
      this.userInfo = info
    },
    
    logout() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
    }
  }
})
```

## 注意事项

1. **跨域问题**: 开发时可能遇到跨域，可以在 `manifest.json` 中配置代理，或使用 HBuilderX 的内置代理
2. **图片上传**: 注意处理上传进度显示和错误处理
3. **Token 过期**: 实现 Token 过期自动跳转登录页
4. **数据缓存**: 合理使用本地缓存，提升用户体验
5. **加载状态**: 所有网络请求都需要显示加载状态
6. **错误处理**: 统一处理网络错误和业务错误
7. **空状态**: 列表为空时显示友好的空状态提示
8. **下拉刷新**: 实现下拉刷新和上拉加载更多
9. **图片预览**: 使用 `uni.previewImage` 实现图片预览
10. **键盘避让**: 输入框聚焦时注意键盘避让

## 测试账号

- 用户名: `admin`
- 密码: `password123`

## 开发环境

- HBuilderX 最新版本
- Node.js 16+
- 微信开发者工具（如果需要编译到微信小程序）

## 编译目标

- H5（浏览器）
- 微信小程序
- APP（iOS/Android）

## 参考资源

- UniApp 官方文档: https://uniapp.dcloud.net.cn/
- uView Plus 文档: https://uviewplus.jiangruyi.com/
- Pinia 文档: https://pinia.vuejs.org/
- Vue 3 官方文档: https://vuejs.org/
