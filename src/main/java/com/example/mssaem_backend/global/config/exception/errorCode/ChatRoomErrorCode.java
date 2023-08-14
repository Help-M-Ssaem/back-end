package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements ErrorCode {
    EMPTY_CHATROOM("CHATMESSAGE_001", "채팅 방이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
