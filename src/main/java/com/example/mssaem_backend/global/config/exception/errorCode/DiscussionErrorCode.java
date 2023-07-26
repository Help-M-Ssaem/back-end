package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DiscussionErrorCode implements ErrorCode {
    EMPTY_DISCUSSION("DISCUSSION_001", "존재하지 않는 토론글입니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
