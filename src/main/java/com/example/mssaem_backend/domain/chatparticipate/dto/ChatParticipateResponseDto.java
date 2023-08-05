package com.example.mssaem_backend.domain.chatparticipate.dto;

import com.example.mssaem_backend.domain.chatmessage.ChatMessage;
import com.example.mssaem_backend.domain.chatparticipate.ChatParticipate;
import com.example.mssaem_backend.domain.member.dto.MemberResponseDto.MemberSimpleInfo;
import com.example.mssaem_backend.global.common.Time;
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
    private String lastMessage;
    private String lastSendAt;
    private MemberSimpleInfo memberSimpleInfo;

    public ChatParticipateRes(ChatParticipate chatParticipate, MemberSimpleInfo memberSimpleInfo, ChatMessage message) {
      this.chatRoomTitle = chatParticipate.getChatRoom().getTitle();
      this.state = chatParticipate.getChatRoom().isState();
      this.lastMessage = message == null ? "" : message.getMessage();
      this.lastSendAt = message == null ? "" : Time.calculateTime(message.getCreatedAt(), 3);
      this.memberSimpleInfo = memberSimpleInfo;
    }

  }
}
