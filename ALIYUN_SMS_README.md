# 阿里云短信验证码服务使用说明

## 功能说明

本项目已集成阿里云短信验证码服务，支持用户注册时的手机号验证功能。

## 功能特性

✅ **发送短信验证码**
- 6 位数字验证码
- 5 分钟有效期
- 60 秒发送间隔限制

✅ **验证验证码**
- 验证码正确性检查
- 过期检查
- 一次性使用（验证后删除）

✅ **安全特性**
- 手机号格式验证（中国大陆手机号）
- 发送频率限制
- 验证码加密存储

## 配置说明

### 1. 获取阿里云短信服务

1. 登录 [阿里云控制台](https://console.aliyun.com/)
2. 开通短信服务
3. 创建签名（如：榴莲写作）
4. 创建短信模板（如：验证码模板）
5. 获取 AccessKey ID 和 AccessKey Secret

### 2. 修改配置文件

编辑 `novel-ai-app/src/main/resources/application.properties`：

```properties
# 阿里云短信配置
aliyun.sms.access-key-id=你的AccessKey ID
aliyun.sms.access-key-secret=你的AccessKey Secret
aliyun.sms.sign-name=你的短信签名
aliyun.sms.template-code=你的短信模板代码
aliyun.sms.enabled=true
aliyun.sms.code-expiration=300
aliyun.sms.send-interval=60
```

### 配置项说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `aliyun.sms.access-key-id` | 阿里云 AccessKey ID | 必填 |
| `aliyun.sms.access-key-secret` | 阿里云 AccessKey Secret | 必填 |
| `aliyun.sms.sign-name` | 短信签名 | 必填 |
| `aliyun.sms.template-code` | 短信模板代码 | 必填 |
| `aliyun.sms.enabled` | 是否启用短信服务 | false |
| `aliyun.sms.code-expiration` | 验证码有效期（秒） | 300（5分钟）|
| `aliyun.sms.send-interval` | 发送间隔（秒） | 60（1分钟）|

## API 接口

### 1. 发送验证码

```
POST /novel/auth/send-code
Content-Type: application/json

请求体:
{
  "phone": "13800138000"
}

响应成功:
{
  "success": true,
  "message": "验证码发送成功"
}

响应失败:
{
  "success": false,
  "message": "发送太频繁，请55秒后重试"
}
```

### 2. 用户注册（带验证码）

```
POST /novel/auth/register
Content-Type: application/json

请求体:
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "phone": "13800138000",
  "verificationCode": "123456",
  "nickname": "测试用户"
}

响应成功:
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

响应失败:
{
  "success": false,
  "message": "验证码错误"
}
```

## 使用流程

### 1. 发送验证码

```bash
curl -X POST http://localhost:8080/novel/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000"}'
```

### 2. 注册用户

```bash
curl -X POST http://localhost:8080/novel/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "phone": "13800138000",
    "verificationCode": "123456",
    "nickname": "测试用户"
  }'
```

## 开发环境

### 禁用短信服务

在开发环境中，可以禁用短信服务，验证码会打印到控制台：

```properties
aliyun.sms.enabled=false
```

启动应用后，发送验证码时会在控制台看到：

```
SMS service is disabled, code for 13800138000 is: 123456
```

### 启用短信服务

在生产环境中，启用短信服务：

```properties
aliyun.sms.enabled=true
```

## 数据库变更

用户表 `users` 新增了 `phone` 字段：

```sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
CREATE INDEX idx_users_phone ON users(phone);
```

由于配置了 `spring.jpa.hibernate.ddl-auto=update`，应用启动时会自动更新数据库结构。

## 错误处理

| 错误信息 | 说明 | 解决方案 |
|----------|------|----------|
| "手机号不能为空" | phone 字段为空 | 检查请求参数 |
| "手机号格式不正确" | 手机号格式错误 | 使用中国大陆手机号（1开头）|
| "验证码不能为空" | verificationCode 字段为空 | 检查请求参数 |
| "验证码必须是6位数字" | 验证码格式错误 | 使用6位数字验证码 |
| "发送太频繁，请X秒后重试" | 发送间隔太短 | 等待指定时间后重试 |
| "验证码已过期或未发送" | 验证码不存在或已过期 | 重新发送验证码 |
| "验证码已过期，请重新发送" | 验证码超过5分钟 | 重新发送验证码 |
| "验证码错误" | 验证码不正确 | 检查验证码 |
| "短信服务未初始化" | 阿里云 SDK 初始化失败 | 检查 AccessKey 配置 |
| "短信发送失败: xxx" | 阿里云发送失败 | 检查签名和模板配置 |

## 短信模板要求

阿里云短信模板需要包含 `code` 变量，例如：

```
您的验证码是${code}，5分钟内有效，请勿泄露。
```

## 注意事项

1. **AccessKey 安全**: 不要将 AccessKey 提交到代码仓库
2. **签名审核**: 短信签名需要阿里云审核通过
3. **模板审核**: 短信模板需要阿里云审核通过
4. **发送限制**: 阿里云有每日发送限制，注意配额
5. **费用**: 短信服务按条计费，注意成本控制

## 测试建议

1. 开发环境使用 `aliyun.sms.enabled=false`
2. 生产环境使用 `aliyun.sms.enabled=true`
3. 测试时使用自己的手机号
4. 注意发送频率限制（60秒一次）

## 扩展功能

本短信服务可以扩展用于：

1. **登录验证码**: 使用相同的短信服务
2. **密码重置**: 使用相同的短信服务
3. **手机号绑定**: 使用相同的短信服务
4. **手机号登录**: 使用相同的短信服务

## 联系方式

如有问题，请联系项目维护者。
