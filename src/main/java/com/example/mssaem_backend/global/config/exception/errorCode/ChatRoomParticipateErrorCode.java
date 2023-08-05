package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomParticipateErrorCode implements ErrorCode {

  EMPTY_CHATPARTICIPATE("CHATROOM_001", "참여한 채팅방이 없습니다.", HttpStatus.NOT_FOUND);
  private final String errorCode;
  private final String message;
  private final HttpStatus status;
}
