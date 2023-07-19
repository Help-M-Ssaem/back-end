package com.example.mssaem_backend.domain.chatroom;

import com.example.mssaem_backend.domain.chatroom.dto.ChatRoomRequestDto.ChatRoomInfo;
import lombok.RequiredArgsConstructor;
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
  @PostMapping("/room")
  public String createRoom(@RequestBody ChatRoomInfo chatRoomInfo) {
    return chatRoomService.createRoom(chatRoomInfo);
  }
}
