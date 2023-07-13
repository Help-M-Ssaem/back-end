package com.example.mssaem_backend.global.config.exception.errorCode;

import com.example.mssaem_backend.global.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WorryBoardErrorCode implements ErrorCode {
  EMPTY_WORRYBOARD("WORRYBOARD_001", "존재하지 않는 게시물입니다.", HttpStatus.NOT_FOUND);


  private final String errorCode;
  private final String message;
  private final HttpStatus status;
}
