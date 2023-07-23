package com.example.mssaem_backend.domain.chat;


import com.example.mssaem_backend.domain.chatmessage.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

  private Long roomId;
  private String message;
  private MessageType type;
  private String accessToken;
}