package com.example.mssaem_backend.domain.chatmessage.dto;


import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatmessage.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatMessageResourceDto {

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatMessageRes {

    private Long chatRoomId;
    private Long worryBoardId;
    private MessageType type;
    private String sender;
    private String message;
    private String createdAt;

    public ChatMessageRes(ChatMessage chatMessage, String createdAt) {
      this.chatRoomId = chatMessage.getChatRoom().getId();
      this.worryBoardId = chatMessage.getChatRoom().getWorryBoardId();
      this.type = chatMessage.getType();
      this.sender = chatMessage.getSender();
      this.message = chatMessage.getMessage();
      this.createdAt = createdAt;
    }
  }
}
