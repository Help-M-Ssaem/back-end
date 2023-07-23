package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

  private final ChatRoomCustomRepository chatRoomCustomRepository;

  public String createRoom(ChatRoomInfo chatRoomInfo) {
    chatRoomCustomRepository.createChatRoom(chatRoomInfo);
    return "채팅방 생성 완료";
  }

  /**
   * Redis에 저장되어 있는 chatRoom 조회
   */
  public List<ChatRoom> selectRedisChatRoom(){
    return chatRoomCustomRepository.findAllRoom();
  }
}
