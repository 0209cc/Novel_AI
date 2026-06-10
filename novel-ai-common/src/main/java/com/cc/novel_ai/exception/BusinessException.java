package com.cc.novel_ai.exception;

import lombok.Getter;

/**
 * 通用业务异常
 * <p>
 * 携带 ErrorCode，由 GlobalExceptionHandler 统一处理
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
