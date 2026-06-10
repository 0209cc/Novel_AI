package com.cc.novel_ai.exception;

import com.cc.novel_ai.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 生成 traceId（取 UUID 前 8 位）
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 业务异常: {} - {}", traceId, ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.valueOf(ex.getErrorCode().getCode() >= 400 && ex.getErrorCode().getCode() < 600
                        ? ex.getErrorCode().getCode() : HttpStatus.BAD_REQUEST.value()))
                .body(ApiResponse.<Void>builder()
                        .code(ex.getErrorCode().getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 404 - 请求路径未找到
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 请求路径不存在: {}", traceId, ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.NOT_FOUND.getCode())
                        .success(false)
                        .message("请求的路径不存在: " + ex.getRequestURL())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 资源未找到: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.NOT_FOUND.getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 请求参数异常
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 请求参数错误: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.BAD_REQUEST.getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 未授权: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.UNAUTHORIZED.getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 禁止访问异常
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbiddenException(ForbiddenException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 禁止访问: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.FORBIDDEN.getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 访问拒绝异常（Spring Security）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 访问被拒绝: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.FORBIDDEN.getCode())
                        .success(false)
                        .message("无权访问该资源")
                        .traceId(traceId)
                        .build());
    }

    /**
     * 认证失败异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 认证失败: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.INVALID_CREDENTIALS.getCode())
                        .success(false)
                        .message("用户名或密码错误")
                        .traceId(traceId)
                        .build());
    }

    /**
     * 资源冲突异常
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(ConflictException ex) {
        String traceId = generateTraceId();
        log.warn("[{}] 资源冲突: {}", traceId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.CONFLICT.getCode())
                        .success(false)
                        .message(ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = generateTraceId();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("[{}] 参数校验失败: {}", traceId, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(ErrorCode.VALIDATION_FAILED.getCode())
                        .success(false)
                        .message("参数校验失败")
                        .data(errors)
                        .traceId(traceId)
                        .build());
    }

    /**
     * 文件存储异常
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileStorageException(FileStorageException ex) {
        String traceId = generateTraceId();
        log.error("[{}] 文件存储错误: {}", traceId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.FILE_STORAGE_ERROR.getCode())
                        .success(false)
                        .message("文件存储失败: " + ex.getMessage())
                        .traceId(traceId)
                        .build());
    }

    /**
     * 其他异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        String traceId = generateTraceId();
        log.error("[{}] 服务器异常: {} - {}", traceId, ex.getClass().getName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.INTERNAL_ERROR.getCode())
                        .success(false)
                        .message("服务器内部错误，请稍后重试")
                        .traceId(traceId)
                        .build());
    }
}
