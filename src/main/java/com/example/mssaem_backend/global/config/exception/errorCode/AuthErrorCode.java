package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    EMPTY_JWT("AUTH_001", "JWT가 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("AUTH_002", "유효하지 않은 JWT입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_MEMBER_JWT("AUTH_003", "만료된 JWT입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_JWT("AUTH_004", "지원하지 않는 JWT입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ID_TOKEN("AUTH_005", "유효하지 않은 ID TOKEN입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("AUTH_006", "유효하지 않은 ACCESS TOKEN입니다.", HttpStatus.BAD_REQUEST),
    FAILED_SOCIAL_LOGIN("AUTH_007", "소셜 로그인에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
