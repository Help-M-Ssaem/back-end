package com.example.mssaem_backend.domain.chatmessage.dto;

import com.amazonaws.services.kms.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatMessageRequestDto {
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatMessageInfo{
    private String content;
    private MessageType type;
    private Long memberId;
    private Long chatRoomId;
  }
}
