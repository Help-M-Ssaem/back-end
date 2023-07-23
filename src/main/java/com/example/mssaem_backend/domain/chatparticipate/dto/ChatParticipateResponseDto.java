package com.example.mssaem_backend.domain.chatparticipate.dto;

import com.example.mssaem_backend.domain.chatparticipate.ChatParticipate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatParticipateResponseDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatParticipateRes {

    private String chatRoomTitle;
    private boolean state;
    private String memberNickName;

    public ChatParticipateRes(ChatParticipate chatParticipate) {
      this.chatRoomTitle = chatParticipate.getChatRoom().getTitle();
      this.state = chatParticipate.getChatRoom().isState();
      this.memberNickName = chatParticipate.getMember().getNickName();
    }

  }
}
