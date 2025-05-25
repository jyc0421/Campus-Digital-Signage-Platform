package com.dddd.authservice.exception;

import com.dddd.authservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一处理服务端异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（如登录失败、用户已存在等）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBusinessException(BusinessException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    /**
     * 处理所有未明确声明的异常（默认 500）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGenericException(Exception ex) {
        return ApiResponse.fail("Internal server error: " + ex.getMessage());
    }
}