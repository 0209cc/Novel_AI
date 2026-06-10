package com.cc.novel_ai.exception;

/**
 * 资源冲突异常（409）
 * <p>
 * 例如：用户名已存在、邮箱已注册等
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
