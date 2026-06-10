package com.cc.novel_ai.exception;

/**
 * 禁止访问异常（403）
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("无权访问该资源");
    }
}
