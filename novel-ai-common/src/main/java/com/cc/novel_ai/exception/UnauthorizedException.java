package com.cc.novel_ai.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
