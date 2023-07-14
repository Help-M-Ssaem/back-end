package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
  private final ChatRoomCustomRepository chatRoomCustomRepository;
  public String createRoom(ChatRoomInfo chatRoomInfo){
    chatRoomCustomRepository.createChatRoom(chatRoomInfo);
    return "채팅방 생성 완료";
  }
}
