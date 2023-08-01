package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatInfo;
import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  /**
   * 채팅방 개설
   */
  @PostMapping("member/chat/rooms")
  public String createRoom(@RequestBody ChatRoomInfo chatRoomInfo) {
    return chatRoomService.createRoom(chatRoomInfo);
  }

  /**
   * Redis에 저장된 채팅방 조회(Test)
   */
  @GetMapping("/redis/rooms")
  public List<ChatRoom> selectChatRoom() {
    return chatRoomService.selectRedisChatRoom();
  }

  /**
   * Redis에 저장한 Enter 조회
   */
  @GetMapping("/redis/enter/{sessionId}")
  public ChatInfo enterChat(@PathVariable("sessionId") String sessionId) {
    return chatRoomService.chatEnter(sessionId);
  }

}
