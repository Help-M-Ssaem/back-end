package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements ErrorCode {

    BOARD_NOT_FOUND("BOARD_001", "게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_MEMBER("BOARD_002","권한이 없는 사용자입니다." , HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
