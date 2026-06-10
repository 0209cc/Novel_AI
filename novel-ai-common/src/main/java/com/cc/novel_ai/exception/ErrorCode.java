package com.cc.novel_ai.exception;

import lombok.Getter;

/**
 * 统一错误码枚举
 */
@Getter
public enum ErrorCode {

    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ========== 业务错误码 1xxx ==========
    VALIDATION_FAILED(1001, "参数校验失败"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    EMAIL_EXISTS(1003, "邮箱已注册"),
    INVALID_CREDENTIALS(1004, "用户名或密码错误"),
    USER_DISABLED(1005, "用户账号已被禁用"),

    // ========== 资源错误码 2xxx ==========
    POST_NOT_FOUND(2001, "帖子不存在"),
    COMMENT_NOT_FOUND(2002, "评论不存在"),
    USER_NOT_FOUND(2003, "用户不存在"),
    FILE_NOT_FOUND(2004, "文件不存在"),

    // ========== 文件错误码 3xxx ==========
    FILE_STORAGE_ERROR(3001, "文件存储失败"),
    FILE_TOO_LARGE(3002, "文件大小超出限制"),
    UNSUPPORTED_FILE_TYPE(3003, "不支持的文件类型");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
