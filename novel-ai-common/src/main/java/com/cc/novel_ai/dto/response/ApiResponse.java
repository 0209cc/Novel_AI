package com.cc.novel_ai.dto.response;

import com.cc.novel_ai.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应包装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 业务错误码（成功时为 200）
     */
    private int code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 异常追踪标识（仅错误响应时返回）
     */
    private String traceId;

    // ==================== 成功响应 ====================

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .success(true)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }

    // ==================== 错误响应（兼容旧接口） ====================

    /**
     * 错误响应（兼容旧接口）
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 错误响应（带数据，兼容旧接口）
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .code(ErrorCode.INTERNAL_ERROR.getCode())
                .success(false)
                .message(message)
                .data(data)
                .build();
    }

    // ==================== 错误响应（使用 ErrorCode） ====================

    /**
     * 错误响应（根据 ErrorCode）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .success(false)
                .message(errorCode.getMessage())
                .build();
    }

    /**
     * 错误响应（根据 ErrorCode + 自定义消息）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 错误响应（根据 ErrorCode + 数据）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .success(false)
                .message(errorCode.getMessage())
                .data(data)
                .build();
    }

    /**
     * 错误响应（根据 ErrorCode + 自定义消息 + 数据）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message, T data) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
