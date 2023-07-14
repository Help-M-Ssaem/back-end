package com.example.mssaem_backend.domain.chatroom.dto;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomResponseDto {
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatRoomRes{
    private String title;
    public ChatRoomRes(ChatRoom chatRoom) {
      this.title = chatRoom.getTitle();
    }
  }
}
