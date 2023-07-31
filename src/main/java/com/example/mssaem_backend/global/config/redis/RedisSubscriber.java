package com.example.mssaem_backend.global.config.redis;

import com.example.mssaem_backend.domain.chatmessage.dto.ChatMessageResourceDto.ChatMessageRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessageSendingOperations messagingTemplate;

  /**
   * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
   */
  public void sendMessage(String publishMessage) {
    try {
      // ChatMessage 객채로 맵핑
      ChatMessageRes chatMessage = objectMapper.readValue(publishMessage, ChatMessageRes.class);
      // Websocket 구독자에게 채팅 메시지 Send
      messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoomId(),
          chatMessage);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
