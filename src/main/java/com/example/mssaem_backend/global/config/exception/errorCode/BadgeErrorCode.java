package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BadgeErrorCode implements ErrorCode {
    EMPTY_BADGE("BADGE_001", "유효하지 않은 뱃지입니다.", HttpStatus.UNAUTHORIZED);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
