package com.example.mssaem_backend.global.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private final HttpStatus status;

    public BaseException(ErrorCode code) {
        errorCode = code.getErrorCode();
        message = code.getMessage();
        status = code.getStatus();
    }
}
