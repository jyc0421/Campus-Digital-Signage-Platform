package com.dddd.deviceservice.exception;

import com.dddd.deviceservice.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<?> handleNotFound(ResourceNotFoundException e) {
        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneric(Exception e) {
        return ApiResponse.fail("Internal Server Error");
    }
}