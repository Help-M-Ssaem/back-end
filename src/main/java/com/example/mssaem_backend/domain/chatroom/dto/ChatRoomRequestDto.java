package com.example.mssaem_backend.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomRequestDto {
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatRoomInfo{
    private String title;
    private Long worryBoardId;
  }
}
