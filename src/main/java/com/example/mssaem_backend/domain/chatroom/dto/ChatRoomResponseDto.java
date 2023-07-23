package com.example.mssaem_backend.domain.chatroom.dto;

import com.example.mssaem_backend.domain.chatroom.ChatRoom;
import com.example.mssaem_backend.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomResponseDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatRoomRes {
    private String chatRoomTitle;
    private boolean state;
    private String memberNickName;
    public ChatRoomRes(ChatRoom chatRoom, Member member) {
        this.chatRoomTitle = chatRoom.getTitle();
        this.state = chatRoom.isState();
        this.memberNickName = member.getNickName();
    }
  }
}
