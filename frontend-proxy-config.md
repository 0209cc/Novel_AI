# 前端代理配置说明

## 问题原因

前端请求 `http://localhost:5173/novel/auth/register` 返回 404，是因为：
- 前端开发服务器运行在 5173 端口
- 后端服务器运行在 8080 端口
- 前端没有配置代理，直接请求了前端服务器

## 解决方案

### 方案 1: 配置 Vite 代理（Vue 3 + Vite）

在 Vue 项目的 `vite.config.js` 中添加代理配置：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/novel': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 方案 2: 配置 Webpack 代理（Vue CLI）

在 `vue.config.js` 中添加：

```javascript
module.exports = {
  devServer: {
    proxy: {
      '/novel': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

### 方案 3: 配置 UniApp 代理

在 `manifest.json` 中添加：

```json
{
  "h5": {
    "devServer": {
      "proxy": {
        "/novel": {
          "target": "http://localhost:8080",
          "changeOrigin": true
        }
      }
    }
  }
}
```

或者在 `vite.config.js` 中（UniApp 使用 Vite 时）：

```javascript
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5173,
    proxy: {
      '/novel': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 方案 4: 直接请求后端地址（临时方案）

在前端代码中直接使用完整的后端地址：

```javascript
// 修改前
const response = await request({
  url: '/auth/register',
  method: 'POST',
  data: formData
})

// 修改后
const response = await request({
  url: 'http://localhost:8080/novel/auth/register',
  method: 'POST',
  data: formData
})
```

或者在请求工具中设置基础 URL：

```javascript
// utils/request.js
const BASE_URL = 'http://localhost:8080/novel'

const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      // ... 其他配置
    })
  })
}
```

## 验证配置

配置完成后，重启前端开发服务器，然后测试：

```bash
# 1. 启动后端
cd E:/Java_Project/Novel_AI
mvn spring-boot:run -pl novel-ai-app

# 2. 启动前端
cd /path/to/your/frontend
npm run dev

# 3. 测试 API
curl -X POST http://localhost:5173/novel/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'
```

## 常见问题

### Q1: 代理配置后不生效

A: 重启前端开发服务器

### Q2: 跨域错误

A: 确保后端 CORS 配置正确，或者使用代理避免跨域

### Q3: 代理配置后端口错误

A: 检查代理目标地址是否正确：`http://localhost:8080`

## 后端地址

后端应用地址：`http://localhost:8080/novel`

API 示例：
- 发送验证码：`http://localhost:8080/novel/auth/send-code`
- 用户注册：`http://localhost:8080/novel/auth/register`
- 用户登录：`http://localhost:8080/novel/auth/login`
