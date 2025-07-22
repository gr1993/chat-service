package com.example.chat_mvc.exception;

import com.example.chat_mvc.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception ex) {
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    private ApiResponse<Object> buildResponseEntity(HttpStatus status, Exception ex) {
        String message = ex.getMessage();
        Throwable cause = ex.getCause();
        if (cause != null && cause.getMessage() != null) {
            message = cause.getMessage();
        }

        log.error("API Error : ", ex);
        return ApiResponse.fail(message);
    }
}
