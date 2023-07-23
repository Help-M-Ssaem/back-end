package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  /**
   * 채팅방 개설
   */
  @PostMapping("member/rooms")
  public String createRoom(@RequestBody ChatRoomInfo chatRoomInfo) {
    return chatRoomService.createRoom(chatRoomInfo);
  }

  /**
   * Redis에 저장된 채팅방 조회(Test)
   */
  @GetMapping("/redis/rooms")
  public List<ChatRoom> selectChatRoom(){
    return chatRoomService.selectRedisChatRoom();
  }
}
