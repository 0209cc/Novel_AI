package com.cc.novel_ai.exception;

/**
 * 请求参数异常
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
