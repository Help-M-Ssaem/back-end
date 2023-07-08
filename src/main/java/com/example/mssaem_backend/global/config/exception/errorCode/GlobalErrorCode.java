package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    NOT_VALID_ARGUMENT_ERROR("GLOBAL_001", "올바른 argument를 입력해주세요.", HttpStatus.BAD_REQUEST),
    NOT_SUPPORTED_URI_ERROR("GLOBAL_002", "올바른 URI로 접근해주세요.", HttpStatus.NOT_FOUND),
    NOT_SUPPORTED_METHOD_ERROR("GLOBAL_003", "지원하지 않는 Method입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    NOT_SUPPORTED_MEDIA_TYPE_ERROR("GLOBAL_004", "지원하지 않는 Media type입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    SERVER_ERROR("GLOBAL_005", "서버와의 연결에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("GLOBAL_006", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
